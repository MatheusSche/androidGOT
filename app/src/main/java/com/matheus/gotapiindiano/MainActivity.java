package com.matheus.gotapiindiano;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.matheus.gotapiindiano.adapter.LivroAdapter;
import com.matheus.gotapiindiano.banco.BDSQLiteHelper;
import com.matheus.gotapiindiano.model.Livro;
import com.matheus.gotapiindiano.network.LivroInterfaceGDS;
import com.matheus.gotapiindiano.network.RetrofitClientLivro;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LivroAdapter adapter;
    private RecyclerView recyclerView;
    private BDSQLiteHelper bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CRIANDO RETROFIT INTERFACE
        LivroInterfaceGDS service = RetrofitClientLivro.getRetrofitInstanceLivro().create(LivroInterfaceGDS.class);
        Call<List<Livro>> call = service.getAllBooks(1, 8);

        call.enqueue(new Callback<List<Livro>>() {
            @Override
            public void onResponse(Call<List<Livro>> call, Response<List<Livro>> response) {
                Toast.makeText(
                        MainActivity.this,
                        "Você está online.",
                        Toast.LENGTH_SHORT).show();
                generateDataListOnline(response.body());
            }

            @Override
            public void onFailure(Call<List<Livro>> call, Throwable t) {
                Toast.makeText(
                        MainActivity.this,
                        "Você está offline. Mostrando registros salvos em banco.",
                        Toast.LENGTH_SHORT).show();

                generateDataListOffline();

            }
        });
    }

    private void generateDataListOffline() {

        bd = new BDSQLiteHelper(MainActivity.this);
        recyclerView = findViewById(R.id.customRecyclerView);
        final ArrayList<Livro> livroList = bd.getAllLivros();
        adapter = new LivroAdapter(this, livroList);
        adapter.setOnItemClickListener(new LivroAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(MainActivity.this, EditarLivroActivity.class);
                intent.putExtra("ID", livroList.get(position).getId());
                startActivity(intent);
                Log.d("a", "onItemClick position: " + position);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Log.d("a", "onItemLongClick pos = " + position);
            }});
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AdicionarLivroActivity.class);
                startActivity(intent);
            }
        });

    }

    private void generateDataListOnline(List<Livro> livroList) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        recyclerView = findViewById(R.id.customRecyclerView);
        adapter = new LivroAdapter(this, livroList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }


}

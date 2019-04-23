package mx.edu.ittepic.themickyebmo.tpdm_u3_practica1_maciasurzuadelia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {
    ListView listaDePeliculas;
    FirebaseAuth autenticacion;
    FirebaseAuth.AuthStateListener verificador;
    FirebaseFirestore baseDeDatos;
    CollectionReference peliculas;
    List<Map> Peli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listaDePeliculas = findViewById(R.id.lista);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        baseDeDatos= FirebaseFirestore.getInstance();
        peliculas = baseDeDatos.collection("mp4");

        autenticacion = FirebaseAuth.getInstance();
        verificador = new  FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario == null || !usuario.isEmailVerified()) {
                    cerrarSesion();
                }
            }
        };

        listaDePeliculas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String nombre = Peli.get(position).get("nombre").toString();
                    aVentanaRegistro(false, nombre);
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aVentanaRegistro(true, "");
            }
        });
    }

    protected void onStart () {
        super.onStart();
        llenarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cerrarSesion) {
            autenticacion.signOut();

        }else{
            if (id==R.id.online){
                startActivity(new Intent(Main2Activity.this,MainActivity.class));
            }
            else{
                startActivity(new Intent(Main2Activity.this,Main2Activity.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }
    private void cerrarSesion () {
        Intent inicioDeSesion = new Intent(Main2Activity.this, Main3Activity.class);
        startActivity(inicioDeSesion);
        finish();
    }

    private void aVentanaRegistro (Boolean esRegistro, String nombre) {
        Intent registro = new Intent(Main2Activity.this, Main5Activity.class);
        registro.putExtra("esRegistro", esRegistro);
        registro.putExtra("nombre", nombre);
        startActivity(registro);
    }

    private void llenarLista() {
        peliculas.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) {
                    miniMensaje("No hay Peliculas para mostrar");
                    return;
                }
                Peli = new ArrayList<>();
                for (QueryDocumentSnapshot temporal: queryDocumentSnapshots) {
                    PeliculaMp4 pm = temporal.toObject(PeliculaMp4.class);
                    Map<String, Object> e = new HashMap<>();
                    e.put("nombre", temporal.getId().toString());
                    e.put("size", pm.getSize());
                    e.put("estreno", pm.getEstreno());

                    Peli.add(e);
                }
                cargarDatos();
            }
        });
    }

    private void cargarDatos () {
        String[] lista = new String[Peli.size()];
        for (int i = 0; i < lista.length; i++) {
            lista[i] = Peli.get(i).get("nombre").toString() + " - " + Peli.get(i).get("size").toString();
        }
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        listaDePeliculas.setAdapter(a);
    }

    private void miniMensaje (String mensaje) {
        Toast.makeText(Main2Activity.this, mensaje, Toast.LENGTH_LONG).show();
    }
}

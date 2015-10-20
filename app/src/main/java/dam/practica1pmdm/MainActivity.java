package dam.practica1pmdm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dam.practica1pmdm.datos.Adaptador;
import dam.practica1pmdm.datos.Contacto;
import dam.practica1pmdm.datos.OrdenarLista;

public class MainActivity extends AppCompatActivity {

    private Adaptador adap;
    private ArrayList<Contacto> a;
    private EditText ed1, ed2, ed3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {

        final ListView lv = (ListView) findViewById(R.id.lvLista);
        final ImageButton bt = (ImageButton) findViewById(R.id.imageButton);
        final Button btOrdenar = (Button) findViewById(R.id.ordenarButton);

        a = new ArrayList<Contacto>();
        a = cargaDatos();
        Collections.sort(a);

        adap = new Adaptador(this, R.layout.item, a);

        registerForContextMenu(lv);
        lv.setAdapter(adap);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adap.detalles(position);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Añadir();
            }
        });

        btOrdenar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Collections.sort(a, new OrdenarLista());
                adap = new Adaptador(MainActivity.this, R.layout.item, a);
                ListView lv = (ListView) findViewById(R.id.lvLista);
                lv.setAdapter(adap);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo vistaInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int posicion = vistaInfo.position;

        switch (item.getItemId()) {
            case R.id.menu_editar:
                editar(posicion);
                return true;
            case R.id.menu_borrar:
                adap.borrar(posicion);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void editar(final int posicion){

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Editar");
        LayoutInflater inflater= LayoutInflater.from(this);

        final View vista = inflater.inflate(R.layout.editar, null);

        alert.setPositiveButton(R.string.editar,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        long id = a.size()-1;
                        EditText et1, et2, et3;
                        final ArrayList<String> listaT;

                        et1 = (EditText) vista.findViewById(R.id.ededit);
                        et2 = (EditText) vista.findViewById(R.id.ededit2);
                        et3 = (EditText) vista.findViewById(R.id.ededit3);

                        String nombre = et1.getText().toString();
                        listaT = new ArrayList<>();
                        listaT.add(et2.getText().toString());
                        listaT.add(et3.getText().toString());

                        a.get(posicion).setNombre(nombre);
                        a.get(posicion).setTelefonos(listaT);

                        adap = new Adaptador(MainActivity.this, R.layout.item, a);
                        ListView lv = (ListView) findViewById(R.id.lvLista);
                        lv.setAdapter(adap);
                    }
                });
        alert.setView(vista);
        alert.setNegativeButton(R.string.dial_atras, null);
        alert.show();
    }

    public void Añadir(){

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Añadir");
        LayoutInflater inflater= LayoutInflater.from(this);

        final View vista = inflater.inflate(R.layout.anadir, null);

        alert.setView(vista);

        alert.setPositiveButton(R.string.aceptar,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        final ArrayList<String> listaT;
                        long id = a.size() - 1;

                        ed1 = (EditText) vista.findViewById(R.id.nombreAn);
                        ed2 = (EditText) vista.findViewById(R.id.telefonoAn);
                        ed3 = (EditText) vista.findViewById(R.id.telefono2An);

                        String nombre = ed1.getText().toString();
                        listaT = new ArrayList<>();
                        listaT.add(ed2.getText().toString());
                        listaT.add(ed3.getText().toString());

                        Contacto nuevo = new Contacto(id, nombre, listaT);

                        a.add(nuevo);

                        adap.notifyDataSetChanged();
                    }
                });

        alert.setNegativeButton(R.string.dial_atras, null);
        alert.show();
    }

    private static ArrayList<Contacto> cargaDatos() {

        ArrayList<Contacto> x = new ArrayList<>();
        ArrayList<String> telf = new ArrayList<>();
        ArrayList<String> telf2 = new ArrayList<>();
        ArrayList<String> telf3 = new ArrayList<>();
        ArrayList<String> telf4 = new ArrayList<>();
        Contacto a,b,c,d;

        a = new Contacto(1,"Pepe", telf);
        x.add(a);
        telf.add("621421342");
        telf.add("623421342");

        b = new Contacto (2,"Juan",telf2);
        telf2.add("621421342");
        x.add(b);

        c = new Contacto(3,"Maria", telf3);
        telf3.add("621421342");
        telf3.add("621826492");
        x.add(c);

        d = new Contacto(4,"Carlos",telf4);
        telf4.add("621421342");
        x.add(d);
        return x;
    }


}

package dam.practica1pmdm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
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
import dam.practica1pmdm.datos.ListaTelefonos;
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

        ListaTelefonos x = new ListaTelefonos(this);
        a = x.getGestion();

        for(Contacto aux:a){
            aux.setTelefonos((ArrayList<String>) x.getListaTelefonos(this,aux.getId()));
        }

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
                añadir();
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

        final EditText et1, et2, et3;
        et1 = (EditText) vista.findViewById(R.id.ededit);
        et2 = (EditText) vista.findViewById(R.id.ededit2);
        et3 = (EditText) vista.findViewById(R.id.ededit3);

        et1.setHint(a.get(posicion).getNombre());
        if(a.get(posicion).getTelefonos().size() == 2) {
            et2.setHint(a.get(posicion).getTelefono(0));
            et3.setHint(a.get(posicion).getTelefono(1));
        }else if (a.get(posicion).getTelefonos().size() == 1){
            et2.setHint(a.get(posicion).getTelefono(0));
        }

        alert.setPositiveButton(R.string.editar,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        long id = a.size() - 1;

                        ArrayList<String> listaT;

                        String nombre = et1.getText().toString();
                        listaT = new ArrayList<>();
                        listaT.add(et2.getText().toString());
                        listaT.add(et3.getText().toString());

                        a.get(posicion).setNombre(nombre);
                        a.get(posicion).setTelefonos(listaT);

                        Collections.sort(a, new OrdenarLista());
                        adap = new Adaptador(MainActivity.this, R.layout.item, a);
                        ListView lv = (ListView) findViewById(R.id.lvLista);
                        lv.setAdapter(adap);
                    }
                });
        alert.setView(vista);
        alert.setNegativeButton(R.string.dial_atras, null);
        alert.show();
    }

    public void añadir(){

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        alert.setTitle("Añadir");
        LayoutInflater inflater= LayoutInflater.from(this);

        final View vista = inflater.inflate(R.layout.anadir, null);

        alert.setView(vista);

        alert.setPositiveButton(R.string.aceptar,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        ArrayList<String> listaT;
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
                        Collections.sort(a, new OrdenarLista());
                        adap = new Adaptador(MainActivity.this, R.layout.item, a);
                        ListView lv = (ListView) findViewById(R.id.lvLista);
                        lv.setAdapter(adap);
                        adap.notifyDataSetChanged();
                    }
                });

        alert.setNegativeButton(R.string.dial_atras, null);
        alert.show();
    }
}

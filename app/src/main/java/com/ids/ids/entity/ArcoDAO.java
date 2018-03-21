package com.ids.ids.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.Key;
import java.util.ArrayList;

import com.ids.ids.utils.DBHelper;

public class ArcoDAO extends DAO<Arco> {

    public static final String TABLE = "Arco";

    public static final String KEY_ID = "id";
    public static final String KEY_nodoPartenzaId = "nodoPartenzaId";
    public static final String KEY_nodoArrivoId = "nodoArrivoId";
    public static final String KEY_mappaId = "mappaId";

    private static ArcoDAO instance = null;

    private NodoDAO nodoDAO;
    private PesoArcoDAO pesoArcoDAO;

    public ArcoDAO(Context context) {
        super(context);
        this.nodoDAO = NodoDAO.getInstance(context);
        this.pesoArcoDAO = PesoArcoDAO.getInstance(context);
    }

    @Override
    protected String getTable() {
        return TABLE;
    }

    @Override
    protected String getIdColumn() {
        return KEY_ID;
    }

    @Override
    protected int getId(Arco arco) {
        return arco.getId();
    }

    @Override
    protected Arco getFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        Nodo nodoPartenza = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoPartenzaId)));
        Nodo nodoArrivo = nodoDAO.find(cursor.getInt(cursor.getColumnIndex(KEY_nodoArrivoId)));

        //TODO TABLE PesoArco (togliere model, aggiungere PesoArcoDAO)
        //TODO no, tenere PesoArco (problemi con il DAO altrimenti), anche in Arco al posto di HashMap
//TODO        ArrayList<PesoArco> pesi = pesoArcoDAO.findAllByColumnValue("idArco", String.valueOf(id)); //TODO idArco nella tabella
        Arco arco = new Arco(id, nodoPartenza, nodoArrivo, null);       //TODO pesi al posto di null
        return arco;
    }

    @Override
    protected void putValues(Arco arco, ContentValues values) {
        values.put(KEY_ID, arco.getId());
        values.put(KEY_nodoPartenzaId, arco.getNodoPartenza().getId());
        values.put(KEY_nodoArrivoId, arco.getNodoArrivo().getId());
        values.put(KEY_mappaId, arco.getMappaId());
    }

    @Override
    protected void cascadeInsert(Arco arco) {
        //TODO inserire pesi
    }

    @Override
    protected void cascadeUpdate(Arco arco) {
        //TODO aggiornare pesi
    }

    @Override
    protected void cascadeDelete(Arco arco) {
        //TODO eliminare pesi
    }

    public static ArcoDAO getInstance(Context context){
        if(instance == null)
            instance = new ArcoDAO(context);
        return instance;
    }
}

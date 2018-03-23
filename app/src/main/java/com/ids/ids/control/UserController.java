package com.ids.ids.control;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Debug;
import android.util.Log;

import com.ids.ids.boundary.BeaconScanner;
import com.ids.ids.boundary.CommunicationServer;
import com.ids.ids.entity.Arco;
import com.ids.ids.entity.Mappa;
import com.ids.ids.entity.Nodo;
import com.ids.ids.entity.NodoDAO;
import com.ids.ids.ui.EmergenzaActivity;
import com.ids.ids.utils.DebugSettings;

import java.util.ArrayList;
import java.util.Random;

public class UserController extends Application{

    public static final int MODALITA_SEGNALAZIONE = 0;
    public static final int MODALITA_EMERGENZA = 1;

    private static UserController instance = null;

    private Activity context;

    private CommunicationServer communicationServer;
    private NodoDAO nodoDAO;
    private ArrayList<Nodo> nodiSelezionati;

    private int modalita;

    private Mappa mappa;

    public UserController(Activity context){
        this.context = context;
        this.communicationServer = CommunicationServer.getInstance(context.getApplicationContext());
        this.nodoDAO = NodoDAO.getInstance(context.getApplicationContext());
        this.nodiSelezionati = new ArrayList<>();
        this.modalita = MODALITA_SEGNALAZIONE;
    }

    /**
     * Verifica che l'utente sia connesso alla rete Wi-Fi
     * @return true se l'utente è connesso al Wi-Fi
     */
    public boolean controllaConnessione(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return !DebugSettings.CHECK_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Verifica che il nodo con un certo id sia selezionato
     * @param idNodo id del nodo da controllare
     * @return true se il nodo con l'id passato è selezionato
     */
    public boolean nodoSelezionato(int idNodo){
        for(Nodo nodo : this.nodiSelezionati)
            if(nodo.getId() == idNodo)
                return true;
        return false;
    }

    /**
     * Aggiunge o rimuove dalla lista dei nodi selezionati il nodo con l'id passato come parametro
     * @param idNodo id del nodo da selezionare o deselezionare
     * @return true se c'è almeno un nodo selezionato
     */
    public boolean selezionaNodo(Nodo nodo){
        nodo.setIncendio();
        if(nodo.isCambiato()) {
            if (!this.nodiSelezionati.contains(nodo))
                this.nodiSelezionati.add(nodo);
        }
        else {
            if (this.nodiSelezionati.contains(nodo))
                this.nodiSelezionati.remove(nodo);
        }
        return !this.nodiSelezionati.isEmpty();
/*
            this.nodiSelezionati.get
        for(Nodo nodo : this.nodiSelezionati){
            if(nodo.getId() == nodoSel.getId()){
                nodo = nodoSel;
                return true;
                //this.nodiSelezionati.remove(nodo);
                //return !this.nodiSelezionati.isEmpty();
            }
        }
        //Nodo nodo = nodoDAO.find(idNodo);
        this.nodiSelezionati.add(nodoSel);
        return true;*/
    }

    /**
     * I nodi selezionati vengono settati nel db locale come sotto incendio,
     * viene fatto lo stesso nel db remoto inviando una richiesta RESTful al server,
     * quindi la lista dei nodi selezionati viene svuotata
     * @return true se l'operazione ha successo
     */
    public boolean inviaNodiSelezionati(){
        for(Nodo nodo : this.nodiSelezionati)
            nodoDAO.update(nodo);
        boolean result = this.communicationServer.inviaNodiSottoIncendio(this.nodiSelezionati);
        if(result)
            this.clearNodiSelezionati();
        return result;
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente in base alla sua posizione rilevata dal beacon
     * @return mappa del piano in cui si trova l'utente
     */
    public Mappa richiediMappa() {
        int piano = 145;// TODO this.beaconScanner.getPianoUtente();
        return this.communicationServer.richiediMappa(piano);
    }

    /**
     * Recupera la mappa del piano in cui si trova l'utente in base alla sua posizione rilevata dal beacon
     *
     */
    public void richiestaMappa(Context contxt,String MacAdrs) {

        //Invio la richiesta al server passandogli la posizione dell utente raffigutrata dell id (MACaddress) del Beacon
        communicationServer.richiestaMappa(contxt,MacAdrs);

    }

    public void MandaEmergenzaActivity(){

        Intent intent = new Intent(context , EmergenzaActivity.class);
        context.startActivity(intent);

    }

    public Nodo getPosizioneUtente() {
        // TODO dummy
        ArrayList<Nodo> nodi = nodoDAO.findAll();
        Random random = new Random();
        return nodoDAO.findAll().get(random.nextInt(nodi.size()));
    }

    public ArrayList<Arco> calcolaPercorso(Mappa mappa, Nodo posUtente) {
        ArrayList<Nodo> uscite = mappa.getNodiUscita();

        // TODO Dijkstra
        




        //TODO dummy
        Random random = new Random();
        ArrayList<Arco> percorso = new ArrayList<>();
        int factor = random.nextInt(3) + 1;
        for (Arco arco : mappa.getArchi())
            if(arco.getId() % factor == 0)
                percorso.add(arco);
        return percorso;
    }

    public int getModalita() {
        return modalita;
    }
    public void setModalita(int modalita) {
        this.modalita = modalita;
    }

    public void clearNodiSelezionati() {
        this.nodiSelezionati.clear();
    }

    public Mappa getMappa() {
        return mappa;
    }

    public void setMappa(Mappa mappa) {
        this.mappa = mappa;
    }

    public static UserController getInstance(Activity context){
        if(instance == null)
            instance = new UserController(context);
        return instance;
    }
}

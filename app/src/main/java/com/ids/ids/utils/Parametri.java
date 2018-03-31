package com.ids.ids.utils;

/**
 * Created by User on 15/03/2018.
 */
public class Parametri {

    private static Parametri istanza;

    // Filtro dispositivi BLE,
    // specificare il nome comune ai beacon a cui ci si vuole collegare
    public final String FILTRO_BLE_DEVICE;
    //Periodo scansione dispositivi BLE
    public final int T_SCAN_PERIOD;
    // periodo scansione localizzatore
    public final int T_POSIZIONE_EMERGENZA;
    // periodo scansione segnalazione
    public final int T_POSIZIONE_SEGNALAZIONE;
    // Hosting Server
    public final String PATH;


    private Parametri() {

        T_SCAN_PERIOD = 2000;
        FILTRO_BLE_DEVICE = "CC2650 SensorTag";
        T_POSIZIONE_EMERGENZA = 3000;
        T_POSIZIONE_SEGNALAZIONE = 5000;
        PATH = "http://192.168.1.4:8080";
    }

    public static Parametri getInstance() {

        if(istanza == null)
            istanza = new Parametri();

        return istanza;

    }
}

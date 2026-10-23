package com.example.bestlocationsemnabouaouina;

public class Utilities {

    public static final String CHANNEL_ID ="MyBestLocations_ID";
    public static String REQUEST_SMS ="envoyer moi votre position";
    public static String Response_SMS ="Ma position est ;";

    public static String IP_SERVEUR="10.0.2.2"; //emulateur;
    /**
     * reseau localisation : IPv4 ipconfig
     * hebergement www......
     */

    public static String URL_GETALL_POSITIONS="http://"+IP_SERVEUR+"/servicephp/getall.php";
}

package br.java.meushoponlineandroid.model;

import android.content.Context;

public class ConexaoDeRede {

    public static final String ERR_DIALOG_TITLE = "Nenhuma conexão com a Internet detectada!";
    private static final String ERR_DIALOG_MSG = "Parece que nosso aplicativo não consegue detectar uma conexão ativa com a Internet, " +
            "verifique as configurações de rede do seu dispositivo.";
    private static final String ERR_DIALOG_POSITIVE_BTN = "Configurações";
    private static final String ERR_DIALOG_NEGATIVE_BTN = "Liberar";

    public static boolean estaConectadoNaInternet(Context context) {
        return true;
    }

    public static boolean estaConectadoNoWifi(Context context) {

        return true;
    }

    public static boolean estaConectadoNaRedeMovel(Context context) {

        return true;
    }

    public static boolean exibirDialogoDeErroSemInternetDisponivel(Context context) {

        return true;
    }
}

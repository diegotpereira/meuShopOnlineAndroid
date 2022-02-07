package br.java.meushoponlineandroid.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarEmail extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Session session;
    private String email;
    private String destinatario;
    private String mensagem;
    private ProgressDialog progressDialog;

    public EnviarEmail(Context context, String email, String destinatario, String mensagem) {
        this.context = context;
        this.email = email;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Mostrando a caixa de diálogo de progresso ao enviar e-mail
        progressDialog = ProgressDialog.show(
                context, "Enviando a mensagem", "Por favor aguarde...",
                false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();

        Toast.makeText(context, "Mensagem enviada", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        //Configurando propriedades para gmail
        //Se você não estiver usando o Gmail, talvez seja necessário alterar os valores
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        // Criando uma nova sessão
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

            //
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
            }
        });

        try {
            MimeMessage mm = new MimeMessage(session);

            //
            mm.setFrom(new InternetAddress(Config.EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(destinatario);
            mm.setText(mensagem);

            Transport.send(mm);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

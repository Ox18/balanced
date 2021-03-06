package com.example.balanced.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.balanced.Entity.User;
import com.example.balanced.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.synap.pay.SynapPayButton;
import com.synap.pay.handler.EventHandler;
import com.synap.pay.handler.payment.SynapAuthorizeHandler;
import com.synap.pay.model.payment.SynapAddress;
import com.synap.pay.model.payment.SynapCardStorage;
import com.synap.pay.model.payment.SynapCountry;
import com.synap.pay.model.payment.SynapCurrency;
import com.synap.pay.model.payment.SynapDocument;
import com.synap.pay.model.payment.SynapFeatures;
import com.synap.pay.model.payment.SynapMetadata;
import com.synap.pay.model.payment.SynapOrder;
import com.synap.pay.model.payment.SynapPerson;
import com.synap.pay.model.payment.SynapProduct;
import com.synap.pay.model.payment.SynapSettings;
import com.synap.pay.model.payment.SynapTransaction;
import com.synap.pay.model.payment.response.SynapAuthorizeResponse;
import com.synap.pay.model.security.SynapAuthenticator;
import com.synap.pay.theming.SynapLightTheme;
import com.synap.pay.theming.SynapTheme;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    LinearLayout linearLayoutButtonClose;

//    private SynapPayButton
    private SynapPayButton paymentWidget;
    private FrameLayout synapForm;
    private Button synapButton;
    private User user;
    private TextView txtWelcome;

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        init();
    }

    private void init(){

        loadProperties();

    }

    private void loadProperties(){
        /////////
        synapForm = findViewById(R.id.contenedorTarjeta);
        synapForm.setVisibility(View.GONE);

        txtWelcome = findViewById(R.id.txtWelcome);

        synapButton = findViewById(R.id.btnPagar);
        synapButton.setVisibility(View.GONE);
        synapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentWidget.pay();
            }
        });

        Button startPayment = findViewById(R.id.btnContinuar);
        startPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
                startPayment.setVisibility(View.GONE);
            }
        });
        ///
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        linearLayoutButtonClose = (LinearLayout)findViewById(R.id.btnClose);

        linearLayoutButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });


        String id = mAuth.getCurrentUser().getUid();

        Gson g = new Gson();

        DatabaseReference docRef = mDatabase.child("Users").child(id);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                user = g.fromJson(dataSnapshot.getValue().toString(), User.class);
            }
        });

    }

    private void startPayment() {
        // Muestre el contenedor del formulario de pago
        synapForm.setVisibility(View.VISIBLE);

        // Muestre el bot??n de pago
        synapButton.setVisibility(View.VISIBLE);

        // Crea el objeto del widget de pago
        this.paymentWidget=SynapPayButton.create(synapForm);

        // Tema de fondo en la tarjeta (Light o Dark)
        SynapTheme theme = new SynapLightTheme(); // Fondo Light con controles dark
        //SynapTheme theme = new SynapDarkTheme(); // Fondo Dark con controles light
        SynapPayButton.setTheme(theme);

        // Seteo del ambiente ".SANDBOX" o ".PRODUCTION"
        SynapPayButton.setEnvironment(SynapPayButton.Environment.SANDBOX);

        // Seteo de los campos de transacci??n
        SynapTransaction transaction=this.buildTransaction();

        // Seteo de los campos de autenticaci??n de seguridad
        SynapAuthenticator authenticator=this.buildAuthenticator(transaction);

        // Control de eventos en el formulario de pago
        SynapPayButton.setListener(new EventHandler() {
            @Override
            public void onEvent(SynapPayButton.Events event) {
                Button paymentButton;
                switch (event){
                    case START_PAY:
                        paymentButton=findViewById(R.id.btnPagar);
                        paymentButton.setVisibility(View.GONE);
                        break;
                    case INVALID_CARD_FORM:
                        paymentButton=findViewById(R.id.btnPagar);
                        paymentButton.setVisibility(View.VISIBLE);
                        break;
                    case VALID_CARD_FORM:
                        break;
                }
            }
        });

        this.paymentWidget.configure(
                // Seteo de autenticaci??n de seguridad y transacci??n
                authenticator,
                transaction,

                // Manejo de la respuesta
                new SynapAuthorizeHandler() {
                    @Override
                    public void success(SynapAuthorizeResponse response) {
                        Looper.prepare();
                        boolean resultAccepted=response.getResult().getAccepted();
                        String resultMessage=response.getResult().getMessage();
                        if (resultAccepted) {
                            // Agregue el c??digo seg??n la experiencia del cliente para la autorizaci??n
                            String id = mAuth.getCurrentUser().getUid();

                            DatabaseReference docRef = mDatabase.child("Users").child(id);
                            user.setPayment_active(true);
                            docRef.setValue(user.getMapData());
                            startActivity(new Intent(PaymentActivity.this, LobbyActivity.class));
                            finish();

                        }
                        else {
                            // Agregue el c??digo seg??n la experiencia del cliente para la denegaci??n
                            showMessage(resultMessage);
                        }
                        Looper.loop();
                    }
                    @Override
                    public void failed(SynapAuthorizeResponse response) {
                        Looper.prepare();
                        String messageText=response.getMessage().getText();
                        // Agregue el c??digo de la experiencia que desee visualizar en un error
                        showMessage(messageText);
                        Looper.loop();
                    }
                }
        );
        Button paymentButton;
        paymentButton=findViewById(R.id.btnPagar);
        paymentButton.setVisibility(View.VISIBLE);
    }

    private SynapTransaction buildTransaction(){
        // Genere el n??mero de orden, este es solo un ejemplo
        String number=String.valueOf(System.currentTimeMillis());

        // Seteo de los datos de transacci??n
        // Referencie al objeto pa??s
        SynapCountry country=new SynapCountry();
        // Seteo del c??digo de pa??s
        country.setCode("PER");

        // Referencie al objeto moneda
        SynapCurrency currency=new SynapCurrency();
        // Seteo del c??digo de moneda
        currency.setCode("PEN");

        //Seteo del monto
        String amount="1.00";

        // Referencie al objeto cliente
        SynapPerson customer=new SynapPerson();
        // Seteo del cliente
        customer.setName("Javier");
        customer.setLastName("P??rez");

        // Referencie al objeto direcci??n del cliente
        SynapAddress address=new SynapAddress();
        // Seteo del pais (country), niveles de ubicaci??n geogr??fica (levels), direcci??n (line1 y line2) y c??digo postal (zip)
        address.setCountry("PER");
        address.setLevels(new ArrayList<String>());
        address.getLevels().add("150000");
        address.getLevels().add("150100");
        address.getLevels().add("150101");
        address.setLine1("Ca Carlos Ferreyros 180");
        address.setZip("15036");
        customer.setAddress(address);

        // Seteo del email y tel??fono
        customer.setEmail("javier.perez@synapsolutions.com");
        customer.setPhone("999888777");

        // Referencie al objeto documento del cliente
        SynapDocument document=new SynapDocument();
        // Seteo del tipo y n??mero de documento
        document.setType("DNI");
        document.setNumber("44556677");
        customer.setDocument(document);

        // Seteo de los datos de env??o
        SynapPerson shipping=customer;
        // Seteo de los datos de facturaci??n
        SynapPerson billing=customer;

        // Referencie al objeto producto
        SynapProduct productItem=new SynapProduct();
        // Seteo de los datos de producto
        productItem.setCode("123");
        productItem.setName("Llavero");
        productItem.setQuantity("1");
        productItem.setUnitAmount("1.00");
        productItem.setAmount("1.00");

        // Referencie al objeto lista de producto
        List<SynapProduct> products=new ArrayList<>();
        // Seteo de los datos de lista de producto
        products.add(productItem);

        // Referencie al objeto metadata
        SynapMetadata metadataItem=new SynapMetadata();
        // Seteo de los datos de metadata
        metadataItem.setName("nombre1");
        metadataItem.setValue("valor1");

        // Referencie al objeto lista de metadata
        List<SynapMetadata> metadataList=new ArrayList<>();
        // Seteo de los datos de lista de metadata
        metadataList.add(metadataItem);

        // Referencie al objeto orden
        SynapOrder order=new SynapOrder();
        // Seteo de los datos de orden
        order.setNumber(number);
        order.setAmount(amount);
        order.setCountry(country);
        order.setCurrency(currency);
        order.setProducts(products);
        order.setCustomer(customer);
        order.setShipping(shipping);
        order.setBilling(billing);
        order.setMetadata(metadataList);

        // Referencie al objeto configuraci??n
        SynapSettings settings=new SynapSettings();
        // Seteo de los datos de configuraci??n
        settings.setBrands(Arrays.asList(new String[]{"VISA","MSCD","AMEX","DINC"}));
        settings.setLanguage("es_PE");
        settings.setBusinessService("MOB");

        // Referencie al objeto transacci??n
        SynapTransaction transaction=new SynapTransaction();
        // Seteo de los datos de transacci??n
        transaction.setOrder(order);
        transaction.setSettings(settings);

        // Feature Card-Storage (Recordar Tarjeta)
        SynapFeatures features=new SynapFeatures();
        SynapCardStorage cardStorage=new SynapCardStorage();

        // Omitir setUserIdentifier, si se trata de usuario an??nimo
        cardStorage.setUserIdentifier("javier.perez@synapsolutions.com");

        features.setCardStorage(cardStorage);
        transaction.setFeatures(features);

        return transaction;
    }

    private SynapAuthenticator buildAuthenticator(SynapTransaction transaction){
        String apiKey="ab254a10-ddc2-4d84-8f31-d3fab9d49520";

        // La signatureKey y la funci??n de generaci??n de firma debe usarse e implementarse en el servidor del comercio utilizando la funci??n criptogr??fica SHA-512
        // solo con prop??sito de demostrar la funcionalidad, se implementar?? en el ejemplo
        // (bajo ninguna circunstancia debe exponerse la signatureKey y la funci??n de firma desde la aplicaci??n porque compromete la seguridad)
        String signatureKey="eDpehY%YPYgsoludCSZhu*WLdmKBWfAo";

        String signature=generateSignature(transaction,apiKey,signatureKey);

        // El campo onBehalf es opcional y se usa cuando un comercio agrupa otros sub comercios
        // la conexi??n con cada sub comercio se realiza con las credenciales del comercio agrupador
        // y enviando el identificador del sub comercio en el campo onBehalf
        //String onBehalf="cf747220-b471-4439-9130-d086d4ca83d4";

        // Referencie el objeto de autenticaci??n
        SynapAuthenticator authenticator=new SynapAuthenticator();

        // Seteo de identificador del comercio (apiKey)
        authenticator.setIdentifier(apiKey);

        // Seteo de firma, que permite verificar la integridad de la transacci??n
        authenticator.setSignature(signature);

        // Seteo de identificador de sub comercio (solo si es un subcomercio)
        //authenticator.setOnBehalf(onBehalf);

        return authenticator;
    }

    // Muestra el mensaje de respuesta
    private void showMessage(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        // Finaliza el intento de pago y regresa al inicio, el comercio define la experiencia del cliente
                        Handler looper = new Handler(getApplicationContext().getMainLooper());
                        looper.post(new Runnable() {
                            @Override
                            public void run() {
                                synapForm.setVisibility(View.GONE);
                                synapButton.setVisibility(View.GONE);
                            }
                        });
                        dialog.cancel();
                    }
                }
        );

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // La signatureKey y la funci??n de generaci??n de firma debe usarse e implementarse en el servidor del comercio utilizando la funci??n criptogr??fica SHA-512
// solo con prop??sito de demostrar la funcionalidad, se implementar?? en el ejemplo
// (bajo ninguna circunstancia debe exponerse la signatureKey y la funci??n de firma desde la aplicaci??n porque compromete la seguridad)
    private String generateSignature(SynapTransaction transaction, String apiKey, String signatureKey){
        String orderNumber=transaction.getOrder().getNumber();
        String currencyCode=transaction.getOrder().getCurrency().getCode();
        String amount=transaction.getOrder().getAmount();

        String rawSignature=apiKey+orderNumber+currencyCode+amount+signatureKey;
        String signature=sha512Hex(rawSignature);
        return signature;
    }

    private String sha512Hex(String value){
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(value.getBytes("UTF-8"));
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void logout(){
        mAuth.signOut();
        loadScreenMain();
    }

    private void loadScreenMain(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}

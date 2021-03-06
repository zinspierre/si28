package com.projet.tony.tx.ChangerLetter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;


import com.projet.tony.tx.R;
import com.projet.tony.tx.background.Affichage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MyActivity2 extends Activity {

    private int sizeFont = 14;

    // counter pour compter le nombres de TextViews de l'historique
    private int counter = 0;

    private String res;
    private ArrayList<TextView> lst_mots = new ArrayList<TextView>();
    // saveWord : sauvegarder les mots de la phrase principale, pour ensuite afficher l'historique
    private ArrayList<String> saveWord = new ArrayList<String>();
    // historique : pour sauvegarder les différents TextViews de l'historique
    private ArrayList<TextView> historique = new ArrayList<TextView>();
    protected  String[] phrase;

    private StringBuilder buff = new StringBuilder();

    private String tester=globalvar_change.getInstance().phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activity2);
        // phrase principale
        //test=getJSONObject("text.JSON");

        run(tester);
        Button raz = (Button)findViewById(R.id.reset);
        Button save = (Button) findViewById(R.id.save);



        raz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historique.clear();
                saveWord.clear();
                phrase = null;
                buff.setLength(0);
                run(tester);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecrireFicher("saves", saveWord);
            }
        });
    }

    public void run(String tester)
    {


        // phrase principale
        LinearLayout l1 = (LinearLayout) findViewById(R.id.linear);
        // historique
        final LinearLayout l2  = (LinearLayout)findViewById(R.id.linear2);


        l1.removeAllViewsInLayout();
        l2.removeAllViewsInLayout();
        res="";

        phrase=get_mots(tester);


        for(int i=0;i<phrase.length;i++)
        {

            // construction de la phrase au chargement
            // par plusieurs TextViews différents
            final TextView txt = new TextView(this);
            final int pos=i;
            txt.setText(phrase[i] + " ");

            //initialisation de saveWord par la phrase de départ
            saveWord.add(phrase[i]);
            // style de la phrase de départ
            txt.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.FILL_PARENT));
            txt.setTextSize(sizeFont);
            txt.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            define_color(txt, phrase[i]);





            // ajoute les TextView contenant chacun
            // un mot de la phrase principale à la view l1
            l1.addView(txt);



            // quand on clique sur un mot
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(get_syno(phrase[pos]).compareTo("")!=0)
                    {

                        txt.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                android.R.anim.slide_in_left));
                        //à chaque clic on aujoute une ligne à l'historique
                        // donc un nouveau textView
                        TextView ol = new TextView(getApplicationContext());
                        // style du TextView
                        ol.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.FILL_PARENT,
                                LinearLayout.LayoutParams.FILL_PARENT));
                        ol.setTextSize((float) (sizeFont));
                        ol.setGravity(Gravity.TOP);
                        ol.setGravity(Gravity.CENTER_HORIZONTAL);
                        ol.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                        ol.setTextAppearance(getApplicationContext(), android.R.style.Animation);
                        ol.setTextColor(Color.BLACK);

                        Typeface font2 = Typeface.createFromAsset(getAssets(), "PRC.ttf");
                        ol.setTypeface(font2);




                        ol.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                        //on ajoute le TextView au 2ème layout
                        l2.addView(ol);


                        //on ajoute ce TextView à l'historique
                        historique.add(ol);

                        // syno : contient le synonyme du mot sur lequel on a cliqué
                        String syno = get_syno(phrase[pos]);
                        phrase[pos] = syno;
                        define_color(txt,syno);

                        //saveWord : contient la phrase du début
                        //puis à chaque changement on insère le mots cliqué
                        //à la position correct
                        saveWord.set(pos, (String) txt.getText());
                        //on change ensuite le textView contenant le mot cliqué dans la phrase principale
                        txt.setText(syno + " ");

                        //tempString : permet d'afficher la phrase en entier dans l'historique
                        // pas en plusieurs TextViews
                        String tempString="";
                        for (int j = 0; j < saveWord.size(); ++j)
                        {
                            //on ajoute tous les éléments de saveWord
                            tempString += " "+saveWord.get(j);
                        }
                        //on ajoute cette phrase au TextView
                        tempString= retirer_accents(tempString);
                        ol.setText(tempString);

                        String change_order = (String) historique.get(historique.size()-1).getText();
                        //historique.get(0).setText(historique.get(historique.size()-1).getText());


                        //on met à jour le mots sur lequel on a cliqué
                        //en mettant le synonyme dans saveWord
                        saveWord.set(pos, (String) txt.getText());
                        //on incrémente le conteur car on a une ligne d'historique en +
                        counter++;

                        for (int j = historique.size()-1; j >=1;--j)
                        {
                            String tmp = (String) historique.get(j-1).getText();
                            historique.get(j).setText(tmp);
                        }
                        historique.get(0).setText(change_order);




                        // suprime une ligne de l'historique quand il y a plus de
                        // 10 ligne à l'historique
                        //if (counter > 3) l2.removeView(historique.get(0));
                        if (counter >= 5)
                        {
                            for (int z= 5; z < historique.size(); ++z)
                                l2.removeView(historique.get(z));
                        }
                    }
                }
            });
        }


    }

    private void ecrireFicher(String nomFichier,ArrayList<String> ph) {
        String s = "";
        for (int i = 0; i < ph.size(); ++i) s = s + " " + ph.get(i);

        File textFolder = new File(Environment.getExternalStorageDirectory(),"OCR/images/");
        if(!textFolder.exists())
            textFolder.mkdir();
        File text = new File(textFolder,nomFichier + ".txt");
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(text,true)));
            writer.write(retirer_accents(s));
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //transforme la phrase en minuscule
    public String[] get_mots(String chaine)
    {
        String[] res=chaine.split(" ");
        for(int i=0;i<res.length;i++)res[i]=res[i].toLowerCase();
        return res;
    }

    public void define_color(TextView param, String mot){
        //affiche les mots non modifiables dans une autre couleur
        Typeface font = Typeface.createFromAsset(getAssets(), "thunder.ttf");

        if (is_in_dico(mot))
        {
            param.setTextColor(Color.BLACK);
            param.setTypeface(font);
            param.setShadowLayer(25, 10, 10, Color.DKGRAY);

        }
        else {
            param.setTextColor(Color.DKGRAY);
            param.setShadowLayer(24, 0, 0, Color.LTGRAY);
        }
    }

    public boolean is_in_dico(String mot){
        return get_syno(mot).compareTo("") != 0;
    }





    public String get_syno(String mot) {
        mot = mot.trim();
        JSONObject test=null;

        test=getJSONObject("dico_"+mot.substring(0,1)+".JSON");

        JSONArray enigme = null;
        String find = "";
        try {
            enigme = test.getJSONArray("liste");
            if(mot.compareTo("balle")==0)Log.d("ballefound",Integer.toString(enigme.length())+"   "+"dico_"+mot.substring(0,1)+".JSON");

            int i = 0;
            while (find.compareTo("") == 0 && i < enigme.length()) {

                JSONObject ret = enigme.getJSONObject(i);
                if (ret.getString("mot").compareTo(mot) == 0)
                {

                    int rand = (int) (Math.random() * (ret.getJSONArray("synonyme").length()));
                    find=ret.getJSONArray("synonyme").getString((int) rand);
                }

                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(find.compareTo("")==0 && mot.substring(mot.length()-1,mot.length()).compareTo("s")==0)
        {
            String mot2=mot.substring(0,mot.length()-1);
            try {
                enigme = test.getJSONArray("liste");

                int i = 0;
                while (find.compareTo("") == 0 && i < enigme.length()) {
                    JSONObject ret = enigme.getJSONObject(i);

                    if (ret.getString("mot").compareTo(mot2) == 0)
                    {
                        int rand = (int) (Math.random() * (ret.getJSONArray("synonyme").length()));
                        find=ret.getJSONArray("synonyme").getString((int) rand);
                    }

                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(find.compareTo("")!=0) find = find + mot.substring(mot.length()-1,mot.length());
        }
        find = find.trim();
        return find;
    }

    public JSONObject getJSONObject(String Fichier)
    {
        buff.setLength(0);
        Log.d("getjsonfile : ",Fichier);
        BufferedReader reader = null;
        JSONObject parser=null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(Fichier)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json;

int sizearr=0;
        try {

            while ((json = reader.readLine()) != null) {

                buff.append(json + "\n");
                sizearr++;
            }
            Log.d("finreadJson","ok : "+Integer.toString(sizearr));

            Log.d("contentfile : "+Fichier+" : ",buff.toString());
            try {
                parser = new JSONObject(buff.toString());
                Log.d("parsingfin","ok");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("parser : "+Fichier+" : ",parser.toString());

        return parser;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.my_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String retirer_accents(String text)
    {

        String source	= text;

        source = source.replaceAll("[èéêë]","e");
        source = source.replaceAll("[àáâãäå]","a");
        source = source.replaceAll("[òóôõöø]","o");
        source = source.replaceAll("[ìíîï]","i");
        source = source.replaceAll("[ùúûü]","u");
        source = source.replaceAll("[ÿ]","y");
        source = source.replaceAll("[ç]","c");
        source = source.replaceAll("[Ç]","C");
        source = source.replaceAll("[°]","-");
        source = source.replaceAll("[Ñ]","N");
        source = source.replaceAll("[ÙÚÛÜ]","U");
        source = source.replaceAll("[ÌÍÎÏ]","I");
        source = source.replaceAll("[ÈÉÊË]","E");
        source = source.replaceAll("[ÒÓÔÕÖØ]","O");
        source = source.replaceAll("[ÀÁÂÃÄÅ]","A");
        return source;
    }


}

package me.nes0x.utils;

import me.nes0x.TxtMaker;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;


public class Updater {


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        URL site = new URL (url);
        HttpURLConnection connection = (HttpURLConnection) site.openConnection();
        connection.setRequestMethod ("GET");
        connection.connect();

        if (connection.getResponseCode() == 200) {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }


        return null;
    }

    public Updater() throws IOException, XmlPullParserException {
        JSONObject json = readJsonFromUrl("https://txtmaker.cf/api/actual-version");

        if (json != null) {
            MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            Model model;
            model = mavenXpp3Reader.read(new
                    InputStreamReader(TxtMaker.class.getResourceAsStream(
                    "/META-INF/maven/me.nes0x/TxtMaker/pom.xml")));

            if (!json.getString("actualVersion").equalsIgnoreCase(model.getVersion())) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Czy chcesz zaaktualizować TxtMaker'a?",
                        "Nowa wersja " + json.getString("actualVersion") + "!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().browse(URI.create("https://txtmaker.cf" + json.getString("actualVersionDownloadLink")));
                }
            }
        }
    }

}



package pl.nesox.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONException;
import org.json.JSONObject;
import pl.nesox.TxtMaker;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

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
        int code = connection.getResponseCode();
        if (!(code != 200)) {
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

    public Updater() throws IOException, ZipException, XmlPullParserException {
        JSONObject json = readJsonFromUrl("https://txtmaker.cf/api/actual-version");

        if (json != null) {

            MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            Model model;

            model = mavenXpp3Reader.read(new
                    InputStreamReader(TxtMaker.class.getResourceAsStream(
                    "/META-INF/maven/pl.nesox/TxtMaker/pom.xml")));


            if (!json.getString("actualVersion").equalsIgnoreCase(model.getVersion())) {
                int result = JOptionPane.showConfirmDialog(
                        TxtMaker.getFrame(),
                        "Czy chcesz pobrać automatycznie nową wersje?",
                        "Nowa wersja " + json.getString("actualVersion") + "!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (result == JOptionPane.YES_OPTION) {
                    File zip = new File(String.valueOf(Paths.get(".", "txtmaker_" + json.getString("actualVersion") + ".zip")));
                    File folder = new File("./");
                    URL url = new URL("https://txtmaker.cf" + json.getString("actualVersionDownloadLink"));

                    try {
                        FileUtils.cleanDirectory(folder);
                    } finally {
                        FileUtils.copyURLToFile(url, zip);

                        ZipFile zipFile = new ZipFile(zip);
                        zipFile.extractAll("./");
                        zip.delete();

                        JOptionPane.showMessageDialog(
                                TxtMaker.getFrame(),
                                "Nowa wersja została pomyślnie zainstalowana.",
                                "Sukces!",
                                JOptionPane.PLAIN_MESSAGE
                        );

                        System.exit(0);
                    }



                }

            }
        }

    }

}

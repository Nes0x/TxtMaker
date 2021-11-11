package pl.nesox.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import pl.nesox.TxtMaker;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class Updater {


    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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

    public Updater() throws IOException, ZipException {
        JSONObject json = readJsonFromUrl("https://txtmaker.cf/api/actual-version");

        if (!json.getString("actualVersion").equalsIgnoreCase("2.0.3")) {
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

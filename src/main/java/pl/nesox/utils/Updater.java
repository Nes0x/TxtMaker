package pl.nesox.utils;

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

    public Updater() throws IOException {
        JSONObject json = readJsonFromUrl("https://txtmaker.cf/api/actual-version");

        if (!json.getString("actualVersion").equalsIgnoreCase("2.0.1")) {
            int result = JOptionPane.showConfirmDialog(
                    TxtMaker.getFrame(),
                    "Czy chcesz pobrać automatycznie do folderu nową wersje?",
                    "Nowa wersja!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                File file = new File(String.valueOf(Paths.get(".", "txtmaker_" + json.getString("actualVersion") + ".zip")));
                URL url = new URL("https://txtmaker.cf" + json.getString("actualVersionDownloadLink"));
                FileUtils.copyURLToFile(url, file);
            }

        }
    }

}

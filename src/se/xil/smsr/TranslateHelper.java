package se.xil.smsr;

import java.io.*;
import java.net.*;
public class TranslateHelper{
    public static String translate(String sl, String tl, String text) throws IOException{
        // fetch
        URL url = new URL("http://translate.google.com.tw/translate_a/t?client=t&hl=en&sl=" +
                sl + "&tl=" + tl + "&ie=UTF-8&oe=UTF-8&multires=1&oc=1&otf=2&ssel=0&tsel=0&sc=1&q=" + 
                URLEncoder.encode(text, "UTF-8"));
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Chrome");
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String result = br.readLine();
        br.close();
        // parse
        // System.out.println(result);
        result = result.substring(2, result.indexOf("]]") + 1);
        StringBuilder sb = new StringBuilder();
        String[] splits = result.split("(?<!\\\\)\"");
        for(int i = 1; i < splits.length; i += 8)
            sb.append(splits[i]);
        return sb.toString().replace("\\n", "\n").replaceAll("\\\\(.)", "$1");
    }
}
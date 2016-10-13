package foobot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class FoobotApplication {
	private static final TreeSet<String> fileArgs = new TreeSet<String>(Arrays.asList("API", "DIR", "LOGIN", "FREQ","UUID","TOKEN"));
	private static final HashSet<String> inputArgs = new HashSet<String>(Arrays.asList("API", "DIR", "LOGIN", "FREQ"));
	private FoobotUI ui;
	private File configFile;
	private HashMap<String, String> config = new HashMap<String, String>();
	private int vueBase;
	
	public FoobotApplication() {
		configFile = new File("configuration.ini");
		try {
			if(!configFile.exists()) {
				configFile.createNewFile();
				initFile();
			} else
				loadConfig();
			vueBase = verifCnx();
			ui = new FoobotUI(vueBase);
			for (String key : inputArgs)
				ui.setInput(key, config.get(key));
			config.put("PASS", "");
			config.put("DEBUT", "");
			config.put("FIN", "");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		activer();
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.setVisible(true);
	}
	
	private void initFile() {
		File f = new java.io.File("");
		config.put("API","eyJhbGciOiJIUzI1NiJ9.eyJncmFudGVlIjoicGF0cmljay5kdWZhdXJlQGZyZWUuZnIiLCJpYXQiOjE0NjA5ODc5NDYsInZhbGlkaXR5IjotMSwianRpIjoiYzYxMWRmM2MtZDJmZC00ZjIyLWE4NzktNTBhZTIyZWE5NjA5IiwicGVybWlzc2lvbnMiOlsidXNlcjpyZWFkIiwiZGV2aWNlOnJlYWQiXSwicXVvdGEiOjIwMCwicmF0ZUxpbWl0Ijo1fQ.YHslIOaKY2ArqiZ_3cnLak8AA6l54da8ezu399I5pFM");
		config.put("DIR", f.getAbsolutePath());
		config.put("FREQ", "1");
		config.put("LOGIN", "patrick.dufaure@free.fr");
		config.put("UUID", "2D09576D83001C80");
		config.put("TOKEN", "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InBhdHJpY2suZHVmYXVyZUBmcmVlLmZyIiwianRpIjoiYjNkMDhiYmYtODJiMy00N2RlLWFkYzMtOTBlZDk2NDM0YTRiIiwiZXhwaXJhdGlvbkRhdGUiOjE0OTk4NDIzODgzMzN9.STu_ciKhP5sRJIzxXlusRqzrFtnicohYkYDT4-nwDN0");
		modifFile();
	}
	
	private void loadConfig() {
		String line;
		String [] frag = new String[2];
		
		try (BufferedReader r = new BufferedReader(new FileReader(configFile));) {
			while ((line = r.readLine()) != null) {
				frag = line.split(":", 2);
				config.put(frag[0], frag[1]);
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void activer() {
		ArrayList<JButton> list;
		
		list = ui.getBoutons(0); // Listeners vue connexion
		list.get(0).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Configuration
				showConfig();
			}
		});
		list.get(1).addActionListener(new ActionListener() { // Quitter
			public void actionPerformed(ActionEvent e) {
				quitter();
			}
		});
		list.get(2).addActionListener(new ActionListener() { // Valider
			public void actionPerformed(ActionEvent e) {
				validCnx();
			}
		});
		
		list = ui.getBoutons(1); // Listeners période
		list.get(0).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Configuration
				showConfig();
			}
		});
		list.get(1).addActionListener(new ActionListener() { // Quitter
			public void actionPerformed(ActionEvent e) {
				quitter();
			}
		});
		list.get(2).addActionListener(new ActionListener() { // Valider
			public void actionPerformed(ActionEvent e) {
				validPeriode();
			}
		});
		
		list = ui.getBoutons(2); // Listeners configuration
		list.get(0).addActionListener(new ActionListener() { // Annuler
			public void actionPerformed(ActionEvent e) {
				selectDir();
			}
		});
		list.get(1).addActionListener(new ActionListener() { // Annuler
			public void actionPerformed(ActionEvent e) {
				cancelConfig();
			}
		});
		list.get(2).addActionListener(new ActionListener() { // Valider
			public void actionPerformed(ActionEvent e) {
				validConfig();
			}
		});
	}
	
	public int	verifCnx() {
		try {
        	String urlLogin = URLEncoder.encode(config.get("LOGIN"), "UTF8");
            URL url = new URL ("https://api.foobot.io/v2/owner/"+urlLogin+"/device/");
            // Ouverture connection HTTP
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestProperty("x-auth-token", config.get("TOKEN"));
            connection.setRequestProperty("X-API-KEY-TOKEN", config.get("API"));
            // Si connexion OK, affichage vue période, sinon affichage vue connexion
            return (connection.getResponseCode() == HttpURLConnection.HTTP_OK)? 1 : 0;
		} catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
	}
	
	private void validCnx() {
		modifConfig("LOGIN");
		modifConfig("PASS");
		try {
        	String urlLogin = URLEncoder.encode(config.get("LOGIN"), "UTF8");
            URL url = new URL ("https://api.foobot.io/v2/user/"+urlLogin+"/login/");
            String msg = "{\"password\":\""+config.get("PASS")+"\"}";
            byte[] output = msg.getBytes("UTF-8");
            // Ouverture connection HTTP
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestProperty("X-API-KEY-TOKEN", config.get("API"));
            // Envoi message 
            OutputStream os = connection.getOutputStream();
            os.write(output);
            os.close();
            // Réception réponse
            int code = connection.getResponseCode();
            if (code == 401) // Erreur : clé API
            	ui.setMsg("Erreur de clé API");
            else if (code != HttpURLConnection.HTTP_OK)
            	ui.setMsg("Erreur d'identification");
            else {
	            InputStream reponse = (InputStream)connection.getInputStream();
	            BufferedReader is = new BufferedReader (new InputStreamReader (reponse));
	            msg = is.readLine();
	            if (msg.equals("false")) // Erreur : mot de passe
	            	ui.setMsg("Erreur d'identification");
	            else { // Pas d'erreur
	            	config.put("TOKEN", connection.getHeaderField("x-auth-token"));
	            	ui.setVue(1);
	        		ui.getInput("DEBUT").requestFocus();
	        		modifFile();
	            }
	            is.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	private void validPeriode() {
		String regex = "^(0?[1-9]|[1-2][0-9]|3[0-1])/(0?[1-9]|1[0-2])/20[1-5][0-9]$";
		String debut = ui.getInput("DEBUT").getText();
		String fin = ui.getInput("FIN").getText();
		String freqTxt = ui.getInput("FREQ").getText();
		if (!debut.matches(regex) || !fin.matches(regex) || !freqTxt.matches("^[1-9][0-9]*$")) {
			ui.setMsg("Erreur de saisie");
			return;
		}
		if (!freqTxt.equals(config.get("FREQ"))) {
			modifConfig("FREQ");
			modifFile();
		}
		try {
			// Récupération timestamp debut et fin
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date date = dateFormat.parse(debut+" 00:00:00");
			Long debutTS = date.getTime()/1000;
			date = dateFormat.parse(fin+" 23:00:00");
			Long finTS = date.getTime()/1000;
			modifConfig("DEBUT");
			modifConfig("FIN");
			int freq = Integer.parseInt(freqTxt)*3600;
			// Préparation URL
            URL url = new URL ("https://api.foobot.io/v2/device/"+config.get("UUID")+"/datapoint/"+debutTS+"/"+finTS+"/"+freq+"/");
            // Ouverture connection HTTP
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setRequestProperty("Accept", "text/csv; charset=UTF-8");
            connection.setRequestProperty("x-auth-token", config.get("TOKEN"));
            connection.setRequestProperty("X-API-KEY-TOKEN", config.get("API"));
            // Réception réponse
            int code = connection.getResponseCode();
            if (code == 400)
            	ui.setMsg("Erreur : Intervale trop grand");
            else if (code != HttpURLConnection.HTTP_OK) // Erreur
            	ui.setMsg("Erreur de traitement");
            else {
	            InputStream reponse = (InputStream)connection.getInputStream();
	            BufferedReader is   = new BufferedReader (new InputStreamReader (reponse));
	            String loc = config.get("DIR")+"\\"+debut.replaceAll("/", "_")+" "+fin.replaceAll("/", "_")+".csv";
	            File fichier = new File(loc);
	            if(!fichier.exists())
	            	fichier.createNewFile();
	            BufferedWriter w = new BufferedWriter(new FileWriter(fichier, false));
	            String line;
	            while ((line = is.readLine()) != null) {
	            	w.write(line.replaceAll(",", ";")+"\n");
	            }
	            is.close();
	            w.close();
	            
	            ui.setMsg("Fichier enregistré sous:"+loc);
	            ui.setInput("DEBUT", "");
	            ui.setInput("FIN", "");
	            ui.getInput("DEBUT").requestFocus();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	private void showConfig() {
		ui.setVue(2);
		ui.getInput("API").requestFocus();
	}
	
	private void validConfig() {
		if (!ui.getInput("API").getText().equals(config.get("API")) || !ui.getInput("DIR").getText().equals(config.get("DIR"))) {
			modifConfig("API");
			modifConfig("DIR");
			modifFile();
		}
		ui.setVue(vueBase);
		ui.getInput((vueBase == 0)? "PASS" : "DEBUT").requestFocus();
	}

	private void cancelConfig() {
		modifText("API");
		modifText("DIR");
		ui.setVue(vueBase);
		ui.getInput((vueBase == 0)? "PASS" : "DEBUT").requestFocus();
	}
	
	private void quitter() {
		System.exit(0);
	}
	
	private void modifText(String cle) {
		ui.setInput(cle, config.get(cle));
	}
	
	private void modifConfig(String cle) {
		config.put(cle, ui.getInput(cle).getText());
	}
	
	private void modifFile() {
		try (BufferedWriter w = new BufferedWriter(new FileWriter(configFile, false));) {
			for (String s : fileArgs)
				w.write(s+":"+config.get(s)+"\n");
			w.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ui.setMsg("Erreur d'initialisation du fichier de configuration");
		}
	}
	
	private void selectDir() {
		JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new java.io.File(ui.getInput("DIR").getText()));
	    chooser.setDialogTitle("Sélectionner dossier");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
	    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		    ui.setInput("DIR", chooser.getSelectedFile().getAbsolutePath());
	    }
	}
}

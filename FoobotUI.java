package foobot;

import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class FoobotUI extends JFrame {
	private static final String [][] labels_text = {
		{"Login", "Mot de passe"}, 
		{"Début (JJ/MM/AAAA)", "Fin (JJ/MM/AAAA)","<html>Fréquence d'échantillonage<br/>(heures)</html>"}, 
		{"Clé API", "Dossier"}};
	private static final String [][] champs_cle = {
		{"LOGIN","PASS"},
		{"DEBUT","FIN","FREQ"},
		{"API", "DIR"}};
	private static final String [][] boutons_text = {
		{"Configuration", "Quitter", "Valider"}, 
		{"Configuration", "Quitter", "Valider"},
		{"Annuler", "Valider"}};
	private static final String [] vues_titres = {"Connexion", "Période", "Configuration"};
	
	private ArrayList<JPanel> vues = new ArrayList<JPanel>();
	private HashMap<String, JTextField> listeInputs = new HashMap<String, JTextField>();
	private ArrayList<ArrayList<JButton>> listeBoutons = new ArrayList<ArrayList<JButton>>();
	private int curVue;
	
	
	public FoobotUI(int vueBase) {
		JPanel vue;
		int i,j, h=0, l=0, len, len2;
		String cle;
		ArrayList<JButton> lBouton;
		JTextField input;
		JButton bouton;
		
		curVue = vueBase;
		len = vues_titres.length;
		for (i=0; i<len; i++){ // Initialisation éléments
			vue = new JPanel(new MigLayout("fillx"));
			lBouton = new ArrayList<JButton>(); // Initialisation boutons
			
			vue.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(vues_titres[i]),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
			vue.setVisible((i==vueBase)? true:false);
			// Initialisation label et inputfield
			len2 = labels_text[i].length;
			for (j=0;j<len2;j++){ 
				cle = champs_cle[i][j];
				vue.add(new JLabel(labels_text[i][j] + ":", JLabel.LEFT));
				if (cle == "PASS") // Mot de passe user
					input = new JPasswordField(30);
				else if (cle == "DIR") { // Sélection répertoire
					bouton = new JButton("Sélectionner");
					lBouton.add(bouton);
					input = new JTextField(30);
					input.setEditable(false);
					input.setFont(new Font("SansSerif", Font.PLAIN, 12));
					vue.add(bouton, "span, split 2");
				} else
					input = new JTextField(30);
				vue.add(input, "wrap 15");
				listeInputs.put(cle, input);
			}
			// Initialisation champs message
			input = new JTextField(30);
			input.setEditable(false);
			input.setVisible(false);
			input.setBorder(null);
			input.setHorizontalAlignment(JTextField.CENTER);
			vue.add(input, "span, center, wrap 20");
			listeInputs.put("MSG"+i, input);
			// Initialisation boutons
			len2 = boutons_text[i].length;
			for (j=0; j<len2;j++){
				bouton = new JButton(boutons_text[i][j]);
				vue.add(bouton, (j!=2)? "span, split "+len2+", center, gapright 10" : "gapleft 10");
				lBouton.add(bouton);
			}
			listeBoutons.add(lBouton);
			h = Math.max(h, vue.getPreferredSize().height);
			l = Math.max(l, vue.getPreferredSize().width);
			vues.add(vue);
		}
		setPreferredSize(new Dimension(l,h+40));
		setContentPane(vues.get(vueBase));
		pack();
		setTitle("FooBot");
	}

	public void setVue(int i) {
		vues.get(curVue).setVisible(false);
		vues.get(i).setVisible(true);
		getMsg(i).setVisible(false);
		setContentPane(vues.get(i));
		curVue = i;
	}

	public JTextField getInput(String cle) {
		return listeInputs.get(cle);
	}
	
	public void setInput (String cle, String val) {
		listeInputs.get(cle).setText(val);
	}
	
	public ArrayList<JButton> getBoutons(int i) {
		return listeBoutons.get(i);
	}
	
	public void setMsg (String txt) {
		getMsg(curVue).setText(txt);
		getMsg(curVue).setVisible(true);
	}
	
	private JTextField getMsg(int i) {
		return listeInputs.get("MSG"+i);
	}
}

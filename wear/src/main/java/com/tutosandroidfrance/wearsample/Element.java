package com.tutosandroidfrance.wearsample;

public class Element {

    private String titre;
    private String texte;
    private int color;
    private String url;

    public Element(String titre, String texte, int color) {
        this.titre = titre;
        this.texte = texte;
        this.color = color;
    }

    public Element(String titre, String texte, String url) {
        this.titre = titre;
        this.texte = texte;
        this.url = url;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

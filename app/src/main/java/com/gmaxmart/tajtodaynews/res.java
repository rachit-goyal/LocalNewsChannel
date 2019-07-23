package com.gmaxmart.tajtodaynews;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by RACHIT on 5/27/2017.
 */
public class res implements Serializable {
    private String imag,time,title,id,author_name,link,content,category,youtube_id,frag_name,eimage,epdf,etitle;
/*    private ArrayList<String> tgs;*/
int localid;
    String VideoName;
    String VideoDesc;
    String url;
    String VideoId;

    public int getLocalid() {
        return localid;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public void setLocalid(int localid) {
        this.localid = localid;
    }

    private ArrayList<id_val> abc;

    public String getFrag_name() {
        return frag_name;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getVideoDesc() {
        return VideoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        VideoDesc = videoDesc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEimage() {
        return eimage;
    }

    public void setEimage(String eimage) {
        this.eimage = eimage;
    }

    public String getEpdf() {
        return epdf;
    }

    public void setEpdf(String epdf) {
        this.epdf = epdf;
    }

    public String getEtitle() {
        return etitle;
    }

    public void setEtitle(String etitle) {
        this.etitle = etitle;
    }

    public void setFrag_name(String frag_name) {
        this.frag_name = frag_name;
    }

    public ArrayList<id_val> getAbc() {
        return abc;
    }

    public void setAbc(ArrayList<id_val> abc) {
        this.abc = abc;
    }




    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor_name() {
        return author_name;
    }


    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImag() {
        return imag;
    }

    public void setImag(String imag) {
        this.imag = imag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

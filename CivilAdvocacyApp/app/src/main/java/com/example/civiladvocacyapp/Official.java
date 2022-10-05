package com.example.civiladvocacyapp;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Official implements Serializable {

    private String officeName, officialName, party, photo;
    private String address, phone, email, website;
    private String facebook, twitter, youtube;

    public Official(String officeN, String officialN, String part, String img,
                    String ad, String ph, String em, String web,
                    String fb, String twit, String yb ) {

        setOfficeName(officeN);
        setOfficialName(officialN);
        setParty(part);
        setPhoto(img);
        setAddress(ad);
        setPhone(ph);
        setEmail(em);
        setWebsite(web);
        setFacebook(fb);
        setTwitter(twit);
        setYoutube(yb);

    }

    public String getOfficeName() { return officeName;}
    public String getOfficialName() { return officialName;}
    public String getParty() { return party;}
    public String getPhoto() { return photo;}
    public String getAddress() { return address;}
    public String getPhone() { return phone;}
    public String getEmail() { return email;}
    public String getWebsite() { return website;}
    public String getFacebook() { return facebook;}
    public String getTwitter() { return twitter;}
    public String getYoutube() { return youtube;}

    public void setOfficeName(String on) { officeName = on;}
    public void setOfficialName(String on) { officialName = on;}
    public void setParty(String p) { party = p;}
    public void setPhoto(String img) { photo = img;}
    public void setAddress(String a) { address = a;}
    public void setPhone(String p) { phone = p;}
    public void setEmail(String e) { email = e;}
    public void setWebsite(String w) { website = w;}
    public void setFacebook(String fb) { facebook = fb;}
    public void setTwitter(String twt) { twitter = twt;}
    public void setYoutube(String yb) { youtube = yb;}

    @NonNull
    @Override
    public String toString() {
        return "\nOffice Name: " + officeName + "\nOfficial Name: " + officialName + "\n\tParty: " + party
                + "\n\tAddress:  " + address + "\n\tPhone: " + phone + "\n\tEmail: " + email
                + "\n\tWebsite: " + website + "\n\tFacebook: " + facebook
                + "\n\tTwitter: " + twitter + "\n\tYoutube:" + youtube;
    }

}

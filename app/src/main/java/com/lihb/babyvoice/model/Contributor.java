package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/1/17.
 */

public class Contributor {

    public String login;
    public String avatar_url;
    public String followers_url;

    public Contributor(String login, String avatar_url, String followers_url) {
        this.login = login;
        this.avatar_url = avatar_url;
        this.followers_url = followers_url;
    }
}

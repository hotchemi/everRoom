package controllers;

import models.Member;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import youroom4j.YouRoom;
import youroom4j.impl.YouRoomFactory;
import youroom4j.model.AccessToken;
import youroom4j.model.User;

public class Application extends Controller {

  public static void index() {
  }

  public static void about() {
  }

  public static void setting() {
    if(!isLogin()) {
      index();
    }
    Long id = Long.parseLong(session.get("sessionKey"));
    Member member = Member.findById(id);
    String email = member.everMail;
    String note = member.note;
    String tag = member.tag;
    String subject = member.title;
    boolean isLogIn = isLogin();
    render(email, note, tag, subject, isLogIn);
  }

  public static void login() {
    YouRoom youRoom = YouRoomFactory.getInstance();
    youRoom.setOAuthConsumer(
        Play.configuration.getProperty("youroom.consumerKey"),
        Play.configuration.getProperty("youroom.consumerSecret"),
        Play.configuration.getProperty("youroom.callback")
    );
    Cache.set("youRoom", youRoom);
    redirect(youRoom.getAuthorizationUrl());
  }

  public static void success(String oauth_verifier) {
    YouRoom youRoom = Cache.get("youRoom", YouRoom.class);
    AccessToken token = youRoom.getAccessToken(oauth_verifier);
    User user = youRoom.verifyCredentials();
    int youRoomId = user.getId();

    Member member = Member.findOrCreate(youRoomId);
    member.youRoomId = youRoomId;
    member.token = token.getAccessToken();
    member.tokenSecret = token.getAcceessTokenSecret();
    member.mail = user.getEmail();
    member.save();

    flash.success("ログインしました｡");
    session.put("sessionKey", member.id);
    setting();
  }

  public static void complete(@Required String email, String note, String tag, @Required String title) {
    if (Validation.hasErrors()) {
      boolean isLogIn = isLogin();
      render("Application/setting.html", isLogIn);
    }
    Long id = Long.parseLong(session.get("sessionKey"));
    Member member = Member.findById(id);
    member.everMail = email;
    member.note = note;
    member.tag = tag;
    member.title = title;
    member.save();

    flash.success("設定を保存しました｡");
    checkLogin();
  }

  public static void delete() {
    long id = Long.parseLong(session.get("sessionKey"));
    Member member = Member.findById(id);
    member.delete();
    session.clear();
    flash.success("配信を停止しました｡");
    index();
  }

  public static void logout() {
    session.clear();
    flash.success("ログアウトしました｡");
    index();
  }

  @Before(unless={"login", "logout", "success", "setting", "complete", "delete"})
  private static void checkLogin() {
    boolean isLogIn = isLogin();
    render(isLogIn);
  }

  private static boolean isLogin() {
    return session.contains("sessionKey");
  }
}
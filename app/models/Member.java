package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class Member extends Model {

  @Required
  public int youRoomId;

  @Required
  public String token;

  @Required
  public String tokenSecret;

  @Required
  public String mail;

  @Required
  public String everMail;

  public String note;

  public String tag;

  @Required
  public String title;

  public Member() {

  }

  public static Member findOrCreate(int youRoomId) {
    Member member = Member.find("youRoomId", youRoomId).first();
    if(member == null) {
      member = new Member();
    }
    return member;
  }

  @Override
  public String toString(){
    return "Member{"+
        "youRoomId=" + youRoomId +
        ", token=" + token +
        ", tokenSecret=" + tokenSecret +
        ", mail=" + mail +
        ", everMail=" + everMail +
        ", note=" + note +
        ", tag=" + tag +
        ", title=" + title + "}";
  }
}
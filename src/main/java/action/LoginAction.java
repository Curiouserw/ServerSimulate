package action;

import simu_anno.ActionAnnotation.Action;

public class LoginAction{
	@Action(name="loginAction")
	public String login(){
		return "Login Successfully!";
	}
}

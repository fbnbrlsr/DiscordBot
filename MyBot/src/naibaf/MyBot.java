package naibaf;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;


public class MyBot {
	public static JDA jda;
	public static String prefix = "?";
	
	// Main method
	public static void main(String[] args) throws LoginException {
		// Have fun with my token ;D
		jda = JDABuilder.createDefault("ODg0NzM3MDM3MDI1MDQyNDc0.YTc1kw.5XERKkE4z8s6gMergrPz3bV1JxA").build();
		jda.getPresence().setStatus(OnlineStatus.IDLE);
		jda.getPresence().setActivity(Activity.playing("type ?help"));
		
		jda.addEventListener(new HangmanGameCommand());
		jda.addEventListener(new GuildMessageReactionDelete());
		jda.addEventListener(new BasicCommands());
		jda.addEventListener(new GuildMemberLeave());
	}
}

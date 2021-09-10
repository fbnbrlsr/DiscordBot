package naibaf;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BasicCommands extends ListenerAdapter {

	public final Set<String> commands = new HashSet<>();

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split("\\s+");

		initializeCommandsSet();
		
		// ignore messages from the bot itself
		if(event.getAuthor().isBot() || HangmanGameCommand.playing){
			return;
		}

		// no command matches
		if(args[0].charAt(0) == MyBot.prefix.charAt(0) && !isCommand(args[0].substring(1))){
			EmbedBuilder error = new EmbedBuilder();

			error.setTitle("Unkown command!");

			error.setDescription("Type `?help` to see all available commmands");
			error.setColor(0xff6b6b);

			event.getChannel().sendMessageEmbeds(error.build()).queue();
			error.clear();

			event.getMessage().addReaction("❌").queue();
		}

		// help command
		else if (args[0].equalsIgnoreCase(MyBot.prefix + "help")) {
			EmbedBuilder help = new EmbedBuilder();
			help.setTitle("Help page");
			help.setDescription("This is a list of all currently available commands.");
			help.setColor(0x7f7ffe);

            help.addField("`?help`", "displays this message with all available commands", false);
			help.addField("`?hello`", "repsonds with a welcoming message", false);
			help.addField("`?info`", "displays some useful information about this bot", false);
			help.addField("`?random <integer>`", "returns a random number between 0 and <integer>", false);
			help.addField("`?ping`", "returns the current ping", false);
			help.addField("`?spammsg <string> <integer>`", "type a message followed by the number of times it should be sent", false);
			help.addField("`?clear <integer>`", "delete a certain amount of sent messages, parameter should be betwenn 0 and 100", false);
			help.addField("`?hangman <[english], [german]>`", "play a game of hangman in the language selected", false);
			help.addField("Delete messages", "React with ❌ on any message sent by you to delete it", false);
			

			help.setFooter("Invoked by " + event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl());
			
			event.getChannel().sendMessageEmbeds(help.build()).queue();
			help.clear();

			addPositiveReaction(event.getMessage());
		}

		// info command
		else if(args[0].equalsIgnoreCase(MyBot.prefix + "info")){
			EmbedBuilder info = new EmbedBuilder();

			info.setTitle("Information about bot naibaf", "https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstley");
			info.setColor(0x7f7ffe);

			info.addField("Creator", "Fabian Brülisauer", false);
			// TODO add uptime, ping

			info.setFooter("Invoked by " + event.getMember().getUser().getAsTag(), event.getMember().getUser().getAvatarUrl());
			
			event.getChannel().sendMessageEmbeds(info.build()).queue();
			info.clear();

			addPositiveReaction(event.getMessage());
		}

		// hello command
        else if(args[0].equalsIgnoreCase(MyBot.prefix + "hello")){
            // event.getChannel().sendMessage("Hi, I'm a Discord bot!").queue();
			
            event.getChannel().sendMessage(event.getGuild().getDefaultChannel().toString()).queue();
        }

		// random command
		else if(args[0].equalsIgnoreCase(MyBot.prefix + "random")){
			int limit;
            try{
				limit = Integer.parseInt(args[1])-1;
			}
			catch(Exception e){
				EmbedBuilder error = new EmbedBuilder();
				error.setTitle("Wrong syntax!");
				error.setDescription("Try `?random <integer>` or type `?help` to see all commands");
				error.setColor(0xff6b6b);
				event.getChannel().sendMessageEmbeds(error.build()).queue();
				error.clear();
				return;
			}

			EmbedBuilder random = new EmbedBuilder();
			random.setTitle("Random number between 0 and " + (limit+1));
			random.setDescription("" + new Random().nextInt(limit));
			random.setColor(0x67ff67);

			event.getChannel().sendMessageEmbeds(random.build()).queue();
			random.clear();

			addPositiveReaction(event.getMessage());
        }

		// ping command
		else if(args[0].equalsIgnoreCase(MyBot.prefix + "ping")){
			EmbedBuilder ping = new EmbedBuilder();

			ping.setTitle("Pong");
			ping.setColor(0x67ff67);

			GatewayPingEvent pe = new GatewayPingEvent(event.getJDA(), 0);

			ping.setDescription(pe.getNewPing() + "ms");

			event.getChannel().sendMessageEmbeds(ping.build()).queue();
			ping.clear();

			addPositiveReaction(event.getMessage());
		}

		// spammsg command
		else if(args[0].equalsIgnoreCase(MyBot.prefix + "spammsg")){
			String msg = "";
			int duplication = 0;
			try{
				msg = args[1];
				duplication = Integer.parseInt(args[2]);
			}
			catch(Exception e){
				EmbedBuilder error = new EmbedBuilder();
				error.setTitle("Wrong syntax!");
				error.setDescription("Try `?spammsg <string> <integer>` or type `?help` to see all commands");
				error.setColor(0xff6b6b);
				event.getChannel().sendMessageEmbeds(error.build()).queue();
				error.clear();
				return;
			}
			for(int i = 0; i < duplication; i++){
				event.getChannel().sendMessage(msg).queue();
			}

			addPositiveReaction(event.getMessage());
		}

		// clear command
		else if(args[0].equalsIgnoreCase(MyBot.prefix + "clear")){
			int amount = 0;
			try{
				amount = Integer.parseInt(args[1]);
			}
			catch(Exception e){
				EmbedBuilder error = new EmbedBuilder();
				error.setTitle("Wrong syntax!");
				error.setDescription("Try `?clear <integer>` or type `?help` to see all commands");
				error.setColor(0xff6b6b);
				event.getChannel().sendMessageEmbeds(error.build()).queue();
				error.clear();
				return;
			}
			try{
				List<Message> messages = event.getChannel().getHistory().retrievePast(amount).complete();
				event.getChannel().deleteMessages(messages).queue();
			}
			catch(Exception e){
				EmbedBuilder error = new EmbedBuilder();
				error.setTitle("Wrong parameter!");
				error.setDescription("Number of messages to delete is too high");
				error.setColor(0xff6b6b);
				event.getChannel().sendMessageEmbeds(error.build()).queue();
				error.clear();
				return;
			}

		}

		
	}

	private void initializeCommandsSet() {
		commands.add("help");
		commands.add("hello");
		commands.add("info");
		commands.add("ping");
		commands.add("spammsg");
		commands.add("random");
		commands.add("g");
		commands.add("hangman");
		commands.add("cancel");
		commands.add("clear");
	}

	public void addPositiveReaction(Message m){
		m.addReaction("✅").queue();
	}

	public boolean isCommand(String c){
		return commands.contains(c);
	}
}

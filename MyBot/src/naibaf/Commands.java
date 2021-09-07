package naibaf;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		if (args[0].equalsIgnoreCase(MyBot.prefix + "info")) {
			EmbedBuilder info = new EmbedBuilder();
			info.setTitle("📺 Television");
			info.setDescription("Completely useless information about a useless bot called 'Television'.");
			info.setColor(0xf45642);
			info.setFooter("Created by techtoolbox", event.getMember().getUser().getAvatarUrl());
			
			event.getChannel().sendMessageEmbeds(info.build()).queue();
			info.clear();
		}
	}
}

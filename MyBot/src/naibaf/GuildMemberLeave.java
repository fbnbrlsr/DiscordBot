package naibaf;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildMemberLeave extends ListenerAdapter{

    // TODO fix this, message is not sent when a user is leaving
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event){
        event.getGuild().getDefaultChannel().sendMessage("test message").queue();
        EmbedBuilder leave = new EmbedBuilder();
        leave.setTitle("User " + event.getMember().getUser().getAsTag() + " left the server.");
        leave.setColor(0xfa200);

        event.getGuild().getDefaultChannel().sendMessageEmbeds(leave.build()).queue();
        leave.clear();
    }


}
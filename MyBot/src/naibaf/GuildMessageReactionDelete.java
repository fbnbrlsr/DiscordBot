package naibaf;

import javax.management.ListenerNotFoundException;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class GuildMessageReactionDelete extends ListenerAdapter{

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        
        if(event.getReactionEmote().getName().equals("❌") && !event.getMember().getUser().equals(event.getJDA().getSelfUser())){
            
            if(event.getMember().getUser().equals(event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor())){
                event.getChannel().retrieveMessageById(event.getMessageId()).complete().delete().queue();
            }
            else{
                event.getReaction().removeReaction().queue();
            }
        }
    }
}
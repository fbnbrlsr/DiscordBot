package naibaf;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Thread.State;
import java.util.*;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HangmanGameCommand extends ListenerAdapter{

    public static String solution;
    public static String current;
    public GuildMessageReceivedEvent event;
    public static boolean playing;
    public static Set<String> guesses;
    public static int tries;
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        this.event = event;

        // syntax: ?hangman <language>
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(MyBot.prefix + "hangmanhelp")){
            EmbedBuilder help = new EmbedBuilder();
            help.setColor(0x7f7ffe);
            help.setTitle("Hangman game help page");
            help.setDescription("Currently playing: " + playing);
            help.addField("Cancel", "Use `?cancel` to end the game", false);
            help.addField("Guess", "Use `?g <character>` to guess a letter or `?g <string>` to guess the entire word", false);
            event.getChannel().sendMessageEmbeds(help.build()).queue();
            help.clear();
            return;
        }

        if(args[0].equalsIgnoreCase(MyBot.prefix + "hangman") && !playing){

            String language;
            guesses = new HashSet<>();
            tries = 12;

            try{
                language = args[1];
            }
            catch(Exception e){
                EmbedBuilder error = new EmbedBuilder();
				error.setTitle("Wrong syntax!");
				error.setDescription("Try `?hangman <[english]>` or type `?help` to see all commands");
				error.setColor(0xff6b6b);
				event.getChannel().sendMessageEmbeds(error.build()).queue();
				error.clear();
				return;
            }

            if(language.equals("english")){
                event.getChannel().sendMessage("english version and word " + solution);
                try{
                    getEnglishWord();
                }
                catch(Exception e){
                    event.getChannel().sendMessage(e.toString()).queue();
                }
            }
            if(language.equals("german")){
                // TODO implement German version
            }

            playing = true;

            EmbedBuilder startedGame = new EmbedBuilder();
            startedGame.setTitle("You started a game of Hangman");
            startedGame.addField("Guess the following word (% represents an unkown letter)", current, false);
            startedGame.setFooter("Use `$g <letter>` to guess a letter or `?g <string>` to guess the entire word");
            startedGame.setColor(0x0000ff);
            event.getChannel().sendMessageEmbeds(startedGame.build()).queue();
            startedGame.clear();

        }

        if(args[0].equalsIgnoreCase(MyBot.prefix + "g")){
            if(playing){
                String guess;
                try{
                    guess = args[1];
                }
                catch(Exception e){
                    EmbedBuilder error = new EmbedBuilder();
                    error.setTitle("Wrong syntax!");
                    error.setDescription("Use `?g <letter>` to guess a letter or `?cancel` to end the game");
                    error.setColor(0xff6b6b);
                    event.getChannel().sendMessageEmbeds(error.build()).queue();
                    error.clear();
                    return;
                }
                if(guess.length() != 1 && guess.length() != solution.length()){
                    EmbedBuilder error = new EmbedBuilder();
                    error.setTitle("Wrong syntax!");
                    error.setDescription("Your guess can only be a single letter or the entire word");
                    error.setColor(0xff6b6b);
                    event.getChannel().sendMessageEmbeds(error.build()).queue();
                    error.clear();
                    return;
                }
                play(guess.toUpperCase());
            }
            else{
                EmbedBuilder startGame = new EmbedBuilder();
                startGame.setTitle("No running game");
                startGame.setDescription("Start a game using `?hangman <[german], [english]>`");
                startGame.setColor(0xff6b6b);
                event.getChannel().sendMessageEmbeds(startGame.build()).queue();
                startGame.clear();
                return;
            }
        }

        if(args[0].equalsIgnoreCase(MyBot.prefix + "cancel") && playing){
            EmbedBuilder cancel = new EmbedBuilder();
            cancel.setTitle("You cancelled your game");
            cancel.setColor(0xff6b6b);
            event.getChannel().sendMessageEmbeds(cancel.build()).queue();
            cancel.clear();
            playing = false;
            return;
        }

    }

    public void getEnglishWord(){
        File englishWords = new File("EnglishWords.txt");
        Scanner s = null;
        Scanner reader = null;
        try {
            s = new Scanner(englishWords);
            
        } catch (FileNotFoundException e) {
            event.getChannel().sendMessage(e.toString()).queue();
            return;
        }

        ArrayList<String> wordList = new ArrayList<>();
        while(s.hasNextLine()){
            wordList.add(s.nextLine());
        }
        solution = wordList.get(new Random().nextInt(wordList.size()-1)).toUpperCase();
        current = "";
        for(int i = 0; i < solution.length(); i++){
            current += "%";
        }
    }

    public void play(String guess){
        if(tries <= 0){
            EmbedBuilder win = new EmbedBuilder();
                win.setTitle("You lost!");
                win.setDescription("The word was " + solution);
                win.setColor(0x0000ff);
                event.getChannel().sendMessageEmbeds(win.build()).queue();
                win.clear();
                playing = false;
                return;
        }
        if(guess.length() > 1){
            tries--;
            if(guess.equalsIgnoreCase(solution)){
                EmbedBuilder win = new EmbedBuilder();
                win.setTitle("You guessed the word!");
                win.setDescription("The word was " + solution);
                win.setFooter("You had " + tries + " guesses left");
                win.setColor(0x0000ff);
                event.getChannel().sendMessageEmbeds(win.build()).queue();
                win.clear();
                playing = false;
                return;
            }
            else{
                EmbedBuilder win = new EmbedBuilder();
                win.setTitle("Your guess is wrong...");
                win.setDescription("You are currently at " + current);
                win.setFooter(tries + " guesses remaining");
                win.setColor(0x0000ff);
                event.getChannel().sendMessageEmbeds(win.build()).queue();
                win.clear();
                return;
            }
        }
        boolean contains = false;
        if(guesses.contains(guess)){
            EmbedBuilder oldGuess = new EmbedBuilder();
            oldGuess.setTitle("You already guessed letter " + guess);
            oldGuess.setColor(0x0000ff);
            event.getChannel().sendMessageEmbeds(oldGuess.build()).queue();
            oldGuess.clear();
            return;
        }

        tries--;
        guesses.add(guess);
        for(int i = 0; i < solution.length(); i++){
            if(guess.equals("" + solution.charAt(i))){
                contains = true;
                current = current.substring(0, i) + guess + current.substring(i+1, solution.length());
            }
        }
        if(contains){
            EmbedBuilder hit = new EmbedBuilder();
            hit.setTitle("HIT");
            hit.addField("Letter " + guess + " was a correct guess", "Your current status is " + current, false);
            hit.setFooter("You have " + tries + " guesses remaining");
            hit.setColor(0x0000ff);
            event.getChannel().sendMessageEmbeds(hit.build()).queue();
            hit.clear();
            return;
        }
        else{
            EmbedBuilder miss = new EmbedBuilder();
            miss.setTitle("MISS");
            miss.addField("Letter " + guess + " was a wrong guess", "Your current status is " + current, false);
            miss.setFooter("You have " + tries + " guesses remaining");
            miss.setColor(0x0000ff);
            event.getChannel().sendMessageEmbeds(miss.build()).queue();
            miss.clear();
            return;
        }
        
    }

    public boolean done(Set<String> guesses){
        for(int i = 0; i < solution.length(); i++){
            if(!guesses.contains("" + solution.charAt(i))){
                return false;
            }
        }
        playing = false;
        return true;
    }

}

package prototype;

/**
 *
 * @author Pablo Alonso
 */
public class Command {
    private final String commandOrder;
    private final String action;
    private final String Program;
    private final String File;

    public Command(String commandOrder, String action, String Program, String File) {
        this.commandOrder = commandOrder;
        this.action = action;
        this.Program = Program;
        this.File = File;
    }

    public String getCommandOrder() {
        return commandOrder;
    }

    public String getAction() {
        return action;
    }

    public String getProgramPath() {
        return Program;
    }

    public String getFilePath() {
        return File;
    }
    
}

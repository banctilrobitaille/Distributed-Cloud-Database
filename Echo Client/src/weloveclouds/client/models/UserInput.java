package weloveclouds.client.models;

/**
 * @author Benoit, Benedek
 */
public class UserInput {
  private String command;
  private String argument;

  protected UserInput(UserInputFactory factory){
    this.command = factory.command;
    this.argument = factory.argument;
  }
  
  public String getCommand() {
    return command;
  }

  public String getArgument() {
    return argument;
  }
  
  public static class UserInputFactory{
    private String command;
    private String argument;
    
    public UserInputFactory command(String command){
      this.command = command;
      return this;
    }
    
    public UserInputFactory arguments(String argument){
      this.argument = argument;
      return this;
    }
    
    public UserInput build(){
      return new UserInput(this);
    }
  }
}
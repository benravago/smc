
import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

class smcContext implements smcActions {

  String FSMname;
  String initialState;
  String packageName;

  class Event {
    String name;
    String nextState;
    List<String> Actions = new ArrayList<>();
  }

  class State {
    String name;
    List<Event> Events = new ArrayList<>();
  }

  List<State> States = new ArrayList<>();

  Set<String> uniqueStates  = new HashSet<>();
  Set<String> uniqueEvents  = new HashSet<>();
  Set<String> uniqueActions = new HashSet<>();

  // - - - - - - - - - - - - - - - - - - - - - - -

  String token;

  State currentState;
  Event currentEvent;

  // Data collection actions

  public void setPackage() { packageName = token; }

  public void setFSM() { FSMname = token; }

  public void setInitialState() { initialState = token; }

  public void setState() {
    currentState = new State();
    currentState.name = token;
    currentState.Events = new ArrayList<>();
    States.add(currentState);
    uniqueStates.add(currentState.name);
  }

  public void setEvent() {
    currentEvent = new Event();
    currentEvent.name = token;
    currentEvent.Actions = new ArrayList<>();
    currentState.Events.add(currentEvent);
    uniqueEvents.add(currentEvent.name);
  }

  public void setNextState() { currentEvent.nextState = token; }

  public void setAction() {
    currentEvent.Actions.add(token);
    uniqueActions.add(token);
  }

  // initial/final actions

  public void setupContext() {
     /* prepare for data collection */
  }

  public void generateCode() {

    String fsmClass = FSMname+"FSM";
    String eventsClass = FSMname+"Events";
    String actionsClass = FSMname+"Actions";
    String classPackage = (packageName != null) ? "package "+packageName+';' : "";

    putfile( fsmClass );

    put( classPackage                                             );
    put( "public class "+fsmClass+" implements "+eventsClass+" {" );
    put( " "+actionsClass+" c;"                                   );
    put( " public "+fsmClass+"("+actionsClass+" c)"+              //
                             "{ this.c=c; t="+initialState+"; }"  );
    put( " STATE t;"                                              );
    put( " void transition(STATE t) { this.t=t; }"                );
    for ( String e : uniqueEvents )
      put(" public void "+e+"() { t."+e+"(); }"                   );

    put( " class STATE {"                                         );
    for ( String e : uniqueEvents )
      put("  void "+e+"() { unexpected(\""+e+"\"); }"             );
    put( "  void unexpected(String e) { "+                        //
              "throw new IllegalStateException("+                 //
              "this.getClass().getName()+'/'+e); }" );
    put( " }"                                                     );

    for ( State s : States ) {
      put( " STATE "+s.name+" = new STATE() {"                    );
      for ( Event e : s.Events ) {
        put( "  void "+e.name+"() {"                              );
        if ( e.nextState != null )
          put( "   transition( "+e.nextState+" );"                );
        for ( String a : e.Actions ) {
          put( "   c."+a+"();"                                    );
        } put( "  }"                                              );
      } put( " };"                                                );
    }

    put( "}"                                                      );

    putfile( actionsClass );

    put( classPackage                                             );
    put( "public interface "+actionsClass+" {"                    );
    for ( String a : uniqueActions )
      put(" void "+a+"();"                                        );
    put( "}"                                                      );

    putfile( eventsClass );

    put( classPackage                                             );
    put( "public interface "+eventsClass+" {"                     );
    for ( String e : uniqueEvents )
      put(" void "+e+"();"                                        );
    put( "}"                                                      );

    putfile(null);
  }

  PrintWriter out;

  void putfile( String f ) {
    if ( f == null )
      out.close();
    else {
      if ( out != null )
        out.close();
      try {
        out = new PrintWriter(new File(f+".java"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  void put( String s ) { out.println(s); }
}

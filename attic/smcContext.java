package smc;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

public class smcContext implements smcActions
{
  String FSMname;
  String initialState;
  String packageName;

  class Event {
    String name;
    String nextState;
    Vector<String> Actions = new Vector<String>();
  }

  class State {
    String name;
    String superState;
    Vector<Event> Events = new Vector<Event>();
  }

  Vector<State> States = new Vector<State>();

  Set<String> uniqueStates  = new HashSet<String>();
  Set<String> uniqueEvents  = new HashSet<String>();
  Set<String> uniqueActions = new HashSet<String>();

  // - - - - - - - - - - - - - - - - - - - - - - -

  public String token;

  State currentState;
  Event currentEvent;

  // Data collection actions

  public void setPackage() { packageName = token; }

  public void setFSM() { FSMname = token; }

  public void setInitialState() { initialState = token; }

  public void setState() {
    currentState = new State();
    currentState.name = token;
    currentState.Events = new Vector<Event>();
    States.add(currentState);
    uniqueStates.add(currentState.name);
  } 

  public void setSuperState() { currentState.superState = token; }

  public void setEvent() {
    currentEvent = new Event();
    currentEvent.name = token;
    currentEvent.Actions = new Vector<String>();
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
    String classPackage = (packageName != null) ? "package "+packageName+';' : "//";

    putfile( fsmClass );

    put( classPackage                                                      );
    put( "public class "+fsmClass+" implements "+eventsClass+" {"     );
    put( " "+actionsClass+" c;"                                       );
    put( " public "+fsmClass+"("+actionsClass+" c)"+                  //
                                 "{ this.c=c; t="+initialState+"; }"  );
    put( " STATE t;"                                                  );
    put( " void transition(STATE t) { this.t=t; }"                    );
    for ( String e : uniqueEvents )
      put(" public void "+e+"() { t."+e+"(); }"                       );

    put( " class STATE {"                                             );
    for ( String e : uniqueEvents )
      put("  void "+e+"() { unexpected(\""+e+"\"); }"                 );
    put( "  void unexpected(String e) { "+                            //
              "throw new RuntimeException("+                          //
              "this.getClass().getName()+'/'+e+\" not expected\"); }" );
    put( " }"                                                         );

    for ( State s : States ) {
      String p = (s.superState != null) ? s.superState : "STATE";
      put( " class _"+s.name+" extends "+p+" {"                       );
      for ( Event e : s.Events ) {
        put( "  void "+e.name+"() { "                                 );
        if ( e.nextState != null )
          put( "   transition( "+e.nextState+" );"                    );
        for ( String a : e.Actions ) {
          put( "   c."+a+"();"                                        );
        } put( "  }"                                                  );
      } put( " }"                                                     );
    }

    for ( String s : uniqueStates )
      put( " _"+s+" "+s+" = new _"+s+"();"                            );

    put( "}"                                                          );

    putfile( actionsClass );

    put( classPackage                                                 );
    put( "public interface "+actionsClass+" {"                        );
    for ( String a : uniqueActions )
      put(" public void "+a+"();"                                     );
    put( "}"                                                          );

    putfile( eventsClass );

    put( classPackage                                                 );
    put( "public interface "+eventsClass+" {"                         );
    for ( String e : uniqueEvents )
      put(" public void "+e+"();"                                     );
    put( "}"                                                          );

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

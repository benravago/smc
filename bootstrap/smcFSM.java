
public class smcFSM implements smcEvents {
 smcActions c;
 public smcFSM(smcActions c){ this.c=c; t=outside; }
 STATE t;
 void transition(STATE t) { this.t=t; }
 public void FSM() { t.FSM(); }
 public void BeginBlock() { t.BeginBlock(); }
 public void State() { t.State(); }
 public void Event() { t.Event(); }
 public void EndBlock() { t.EndBlock(); }
 public void Package() { t.Package(); }
 public void Initial() { t.Initial(); }
 public void Name() { t.Name(); }
 class STATE {
  void FSM() { unexpected("FSM"); }
  void BeginBlock() { unexpected("BeginBlock"); }
  void State() { unexpected("State"); }
  void Event() { unexpected("Event"); }
  void EndBlock() { unexpected("EndBlock"); }
  void Package() { unexpected("Package"); }
  void Initial() { unexpected("Initial"); }
  void Name() { unexpected("Name"); }
  void unexpected(String e) { throw new IllegalStateException(this.getClass().getName()+'/'+e); }
 }
 STATE outside = new STATE() {
  void Package() {
   transition( newPackage );
  }
  void FSM() {
   transition( newFSM );
  }
 };
 STATE newPackage = new STATE() {
  void Name() {
   transition( outside );
   c.setPackage();
  }
 };
 STATE newFSM = new STATE() {
  void Name() {
   transition( inFSM );
   c.setFSM();
  }
 };
 STATE inFSM = new STATE() {
  void BeginBlock() {
   transition( toState );
  }
 };
 STATE toState = new STATE() {
  void State() {
   transition( newState );
  }
  void EndBlock() {
   transition( outside );
   c.generateCode();
  }
 };
 STATE newState = new STATE() {
  void Name() {
   transition( inState );
   c.setState();
  }
 };
 STATE inState = new STATE() {
  void Initial() {
   transition( inState );
   c.setInitialState();
  }
  void BeginBlock() {
   transition( toEvent );
  }
 };
 STATE toEvent = new STATE() {
  void Event() {
   transition( newEvent );
  }
  void EndBlock() {
   transition( toState );
  }
 };
 STATE newEvent = new STATE() {
  void Name() {
   transition( inEvent );
   c.setEvent();
  }
 };
 STATE inEvent = new STATE() {
  void Name() {
   transition( inEvent );
   c.setNextState();
  }
  void BeginBlock() {
   transition( inAction );
  }
 };
 STATE inAction = new STATE() {
  void Name() {
   transition( inAction );
   c.setAction();
  }
  void EndBlock() {
   transition( toEvent );
  }
 };
}

package smc;
public class smcFSM implements smcEvents {
 smcActions c;
 public smcFSM(smcActions c){ this.c=c; t=outside; }
 STATE t;
 void transition(STATE t) { this.t=t; }
 public void Name() { t.Name(); }
 public void FSM() { t.FSM(); }
 public void EndBlock() { t.EndBlock(); }
 public void State() { t.State(); }
 public void Extends() { t.Extends(); }
 public void BeginBlock() { t.BeginBlock(); }
 public void Event() { t.Event(); }
 public void Package() { t.Package(); }
 public void Initial() { t.Initial(); }
 class STATE {
  void Name() { unexpected("Name"); }
  void FSM() { unexpected("FSM"); }
  void EndBlock() { unexpected("EndBlock"); }
  void State() { unexpected("State"); }
  void Extends() { unexpected("Extends"); }
  void BeginBlock() { unexpected("BeginBlock"); }
  void Event() { unexpected("Event"); }
  void Package() { unexpected("Package"); }
  void Initial() { unexpected("Initial"); }
  void unexpected(String e) { throw new RuntimeException(this.getClass().getName()+'/'+e+" not expected"); }
 }
 class _outside extends STATE {
  void Package() { 
   transition( newPackage );
  }
  void FSM() { 
   transition( newFSM );
  }
 }
 class _newPackage extends STATE {
  void Name() { 
   transition( outside );
   c.setPackage();
  }
 }
 class _newFSM extends STATE {
  void Name() { 
   transition( inFSM );
   c.setFSM();
  }
 }
 class _inFSM extends STATE {
  void BeginBlock() { 
   transition( toState );
  }
 }
 class _toState extends STATE {
  void State() { 
   transition( newState );
  }
  void EndBlock() { 
   transition( outside );
   c.generateCode();
  }
 }
 class _newState extends STATE {
  void Name() { 
   transition( inState );
   c.setState();
  }
 }
 class _inState extends STATE {
  void Initial() { 
   transition( inState );
   c.setInitialState();
  }
  void Extends() { 
   transition( inExtend );
  }
  void BeginBlock() { 
   transition( toEvent );
  }
 }
 class _inExtend extends STATE {
  void Name() { 
   transition( inState );
   c.setSuperState();
  }
 }
 class _toEvent extends STATE {
  void Event() { 
   transition( newEvent );
  }
  void EndBlock() { 
   transition( toState );
  }
 }
 class _newEvent extends STATE {
  void Name() { 
   transition( inEvent );
   c.setEvent();
  }
 }
 class _inEvent extends STATE {
  void Name() { 
   transition( inEvent );
   c.setNextState();
  }
  void BeginBlock() { 
   transition( inAction );
  }
 }
 class _inAction extends STATE {
  void Name() { 
   transition( inAction );
   c.setAction();
  }
  void EndBlock() { 
   transition( toEvent );
  }
 }
 _inState inState = new _inState();
 _inAction inAction = new _inAction();
 _toState toState = new _toState();
 _inEvent inEvent = new _inEvent();
 _newPackage newPackage = new _newPackage();
 _inExtend inExtend = new _inExtend();
 _inFSM inFSM = new _inFSM();
 _newEvent newEvent = new _newEvent();
 _newState newState = new _newState();
 _toEvent toEvent = new _toEvent();
 _newFSM newFSM = new _newFSM();
 _outside outside = new _outside();
}

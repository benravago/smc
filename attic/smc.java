package smc;

import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;

public class smc { public static void main( String args[] ) throws Exception {

  String key, value;
  Map<String,String> symbols = new HashMap<String,String>();

  smcContext c = new smcContext();
  smcFSM f = new smcFSM(c);

  FileReader rd = new FileReader(args[0]);
  StreamTokenizer st = new StreamTokenizer(rd);
  
  st.slashSlashComments(true);
  st.slashStarComments(true);
  
  while ( st.nextToken() != StreamTokenizer.TT_EOF ) {
    switch (st.ttype) {

      case StreamTokenizer.TT_WORD:
        if ( "event"    .equals(st.sval)) f.Event();   else
        if ( "state"    .equals(st.sval)) f.State();   else
        if ( "extends"  .equals(st.sval)) f.Extends(); else
        if ( "initial"  .equals(st.sval)) f.Initial(); else
        if ( "fsm"      .equals(st.sval)) f.FSM();     else
        if ( "package"  .equals(st.sval)) f.Package(); else
        /* symbol */ { c.token = st.sval; f.Name();    }
        break;

      case '{':
        f.BeginBlock();
        break;

      case '}':
        f.EndBlock();
        break;

      case '#':
        st.nextToken(); key = st.sval;
        st.nextToken(); value = st.sval;
        symbols.put(key,value);
        break;

      case '"':; // look up quoted symbols
      case '\'':
        c.token = symbols.get(st.sval);
        f.Name();
        break;

      default:
        break;
    }
  }
  rd.close();
  
}}

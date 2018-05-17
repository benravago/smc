
import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class smc {
public static void main(String args[]) throws Exception {

  String key, value;
  Map<String,String> symbols = new HashMap<>();

  smcContext c = new smcContext();
  smcFSM f = new smcFSM(c);

  StreamTokenizer st = new StreamTokenizer(
    Files.newBufferedReader(Paths.get(args[0])));

  st.slashSlashComments(true);
  st.slashStarComments(true);

  while ( st.nextToken() != StreamTokenizer.TT_EOF ) {
    switch (st.ttype) {

      case StreamTokenizer.TT_WORD:
        switch (st.sval) {
          case "event"   : f.Event();   break;
          case "state"   : f.State();   break;
          case "initial" : f.Initial(); break;
          case "fsm"     : f.FSM();     break;
          case "package" : f.Package(); break;

          default: c.token = st.sval; f.Name(); break;
        }
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
}}

// $ANTLR 3.5 D:\\projects\\CodeStory\\Math.g 2013-01-15 08:17:42

package com.ninja_squad.jb.codestory.action;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings("all")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(justification = "generated code")
public class MathLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__5=5;
	public static final int T__6=6;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int FLOAT=4;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public MathLexer() {} 
	public MathLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public MathLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "D:\\projects\\CodeStory\\Math.g"; }

	// $ANTLR start "T__5"
	public final void mT__5() throws RecognitionException {
		try {
			int _type = T__5;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:2:6: ( '(' )
			// D:\\projects\\CodeStory\\Math.g:2:8: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__5"

	// $ANTLR start "T__6"
	public final void mT__6() throws RecognitionException {
		try {
			int _type = T__6;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:3:6: ( ')' )
			// D:\\projects\\CodeStory\\Math.g:3:8: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__6"

	// $ANTLR start "T__7"
	public final void mT__7() throws RecognitionException {
		try {
			int _type = T__7;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:4:6: ( '*' )
			// D:\\projects\\CodeStory\\Math.g:4:8: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__7"

	// $ANTLR start "T__8"
	public final void mT__8() throws RecognitionException {
		try {
			int _type = T__8;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:5:6: ( '+' )
			// D:\\projects\\CodeStory\\Math.g:5:8: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__8"

	// $ANTLR start "T__9"
	public final void mT__9() throws RecognitionException {
		try {
			int _type = T__9;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:6:6: ( '-' )
			// D:\\projects\\CodeStory\\Math.g:6:8: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__9"

	// $ANTLR start "T__10"
	public final void mT__10() throws RecognitionException {
		try {
			int _type = T__10;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:7:7: ( '/' )
			// D:\\projects\\CodeStory\\Math.g:7:9: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__10"

	// $ANTLR start "FLOAT"
	public final void mFLOAT() throws RecognitionException {
		try {
			int _type = FLOAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\projects\\CodeStory\\Math.g:37:7: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ )
			int alt4=2;
			alt4 = dfa4.predict(input);
			switch (alt4) {
				case 1 :
					// D:\\projects\\CodeStory\\Math.g:37:11: ( '0' .. '9' )+ '.' ( '0' .. '9' )+
					{
					// D:\\projects\\CodeStory\\Math.g:37:11: ( '0' .. '9' )+
					int cnt1=0;
					loop1:
					while (true) {
						int alt1=2;
						int LA1_0 = input.LA(1);
						if ( ((LA1_0 >= '0' && LA1_0 <= '9')) ) {
							alt1=1;
						}

						switch (alt1) {
						case 1 :
							// D:\\projects\\CodeStory\\Math.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt1 >= 1 ) break loop1;
							EarlyExitException eee = new EarlyExitException(1, input);
							throw eee;
						}
						cnt1++;
					}

					match('.'); 
					// D:\\projects\\CodeStory\\Math.g:37:25: ( '0' .. '9' )+
					int cnt2=0;
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( ((LA2_0 >= '0' && LA2_0 <= '9')) ) {
							alt2=1;
						}

						switch (alt2) {
						case 1 :
							// D:\\projects\\CodeStory\\Math.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt2 >= 1 ) break loop2;
							EarlyExitException eee = new EarlyExitException(2, input);
							throw eee;
						}
						cnt2++;
					}

					}
					break;
				case 2 :
					// D:\\projects\\CodeStory\\Math.g:38:13: ( '0' .. '9' )+
					{
					// D:\\projects\\CodeStory\\Math.g:38:13: ( '0' .. '9' )+
					int cnt3=0;
					loop3:
					while (true) {
						int alt3=2;
						int LA3_0 = input.LA(1);
						if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
							alt3=1;
						}

						switch (alt3) {
						case 1 :
							// D:\\projects\\CodeStory\\Math.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt3 >= 1 ) break loop3;
							EarlyExitException eee = new EarlyExitException(3, input);
							throw eee;
						}
						cnt3++;
					}

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FLOAT"

	@Override
	public void mTokens() throws RecognitionException {
		// D:\\projects\\CodeStory\\Math.g:1:8: ( T__5 | T__6 | T__7 | T__8 | T__9 | T__10 | FLOAT )
		int alt5=7;
		switch ( input.LA(1) ) {
		case '(':
			{
			alt5=1;
			}
			break;
		case ')':
			{
			alt5=2;
			}
			break;
		case '*':
			{
			alt5=3;
			}
			break;
		case '+':
			{
			alt5=4;
			}
			break;
		case '-':
			{
			alt5=5;
			}
			break;
		case '/':
			{
			alt5=6;
			}
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			{
			alt5=7;
			}
			break;
		default:
			NoViableAltException nvae =
				new NoViableAltException("", 5, 0, input);
			throw nvae;
		}
		switch (alt5) {
			case 1 :
				// D:\\projects\\CodeStory\\Math.g:1:10: T__5
				{
				mT__5(); 

				}
				break;
			case 2 :
				// D:\\projects\\CodeStory\\Math.g:1:15: T__6
				{
				mT__6(); 

				}
				break;
			case 3 :
				// D:\\projects\\CodeStory\\Math.g:1:20: T__7
				{
				mT__7(); 

				}
				break;
			case 4 :
				// D:\\projects\\CodeStory\\Math.g:1:25: T__8
				{
				mT__8(); 

				}
				break;
			case 5 :
				// D:\\projects\\CodeStory\\Math.g:1:30: T__9
				{
				mT__9(); 

				}
				break;
			case 6 :
				// D:\\projects\\CodeStory\\Math.g:1:35: T__10
				{
				mT__10(); 

				}
				break;
			case 7 :
				// D:\\projects\\CodeStory\\Math.g:1:41: FLOAT
				{
				mFLOAT(); 

				}
				break;

		}
	}


	protected DFA4 dfa4 = new DFA4(this);
	static final String DFA4_eotS =
		"\1\uffff\1\3\2\uffff";
	static final String DFA4_eofS =
		"\4\uffff";
	static final String DFA4_minS =
		"\1\60\1\56\2\uffff";
	static final String DFA4_maxS =
		"\2\71\2\uffff";
	static final String DFA4_acceptS =
		"\2\uffff\1\1\1\2";
	static final String DFA4_specialS =
		"\4\uffff}>";
	static final String[] DFA4_transitionS = {
			"\12\1",
			"\1\2\1\uffff\12\1",
			"",
			""
	};

	static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
	static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
	static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
	static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
	static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
	static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
	static final short[][] DFA4_transition;

	static {
		int numStates = DFA4_transitionS.length;
		DFA4_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
		}
	}

	protected class DFA4 extends DFA {

		public DFA4(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 4;
			this.eot = DFA4_eot;
			this.eof = DFA4_eof;
			this.min = DFA4_min;
			this.max = DFA4_max;
			this.accept = DFA4_accept;
			this.special = DFA4_special;
			this.transition = DFA4_transition;
		}
		@Override
		public String getDescription() {
			return "37:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ );";
		}
	}

}

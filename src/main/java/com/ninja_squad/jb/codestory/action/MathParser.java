// $ANTLR 3.5 D:\\projects\\CodeStory\\Math.g 2013-01-15 08:17:42

package com.ninja_squad.jb.codestory.action;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("all")
public class MathParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "FLOAT", "'('", "')'", "'*'", 
		"'+'", "'-'", "'/'"
	};
	public static final int EOF=-1;
	public static final int T__5=5;
	public static final int T__6=6;
	public static final int T__7=7;
	public static final int T__8=8;
	public static final int T__9=9;
	public static final int T__10=10;
	public static final int FLOAT=4;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public MathParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public MathParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return MathParser.tokenNames; }
	@Override public String getGrammarFileName() { return "D:\\projects\\CodeStory\\Math.g"; }





	// $ANTLR start "expr"
	// D:\\projects\\CodeStory\\Math.g:13:1: expr returns [BigDecimal value] : e= multExpr ( '+' e= multExpr | '-' e= multExpr )* ;
	public final BigDecimal expr() {
		BigDecimal value = null;


		BigDecimal e =null;

		try {
			// D:\\projects\\CodeStory\\Math.g:14:5: (e= multExpr ( '+' e= multExpr | '-' e= multExpr )* )
			// D:\\projects\\CodeStory\\Math.g:14:9: e= multExpr ( '+' e= multExpr | '-' e= multExpr )*
			{
			pushFollow(FOLLOW_multExpr_in_expr36);
			e=multExpr();
			state._fsp--;

			value = e;
			// D:\\projects\\CodeStory\\Math.g:15:9: ( '+' e= multExpr | '-' e= multExpr )*
			loop1:
			while (true) {
				int alt1=3;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==8) ) {
					alt1=1;
				}
				else if ( (LA1_0==9) ) {
					alt1=2;
				}

				switch (alt1) {
				case 1 :
					// D:\\projects\\CodeStory\\Math.g:15:13: '+' e= multExpr
					{
					match(input,8,FOLLOW_8_in_expr52); 
					pushFollow(FOLLOW_multExpr_in_expr58);
					e=multExpr();
					state._fsp--;

					value = value.add(e);
					}
					break;
				case 2 :
					// D:\\projects\\CodeStory\\Math.g:16:13: '-' e= multExpr
					{
					match(input,9,FOLLOW_9_in_expr74); 
					pushFollow(FOLLOW_multExpr_in_expr80);
					e=multExpr();
					state._fsp--;

					value = value.subtract(e);
					}
					break;

				default :
					break loop1;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "expr"



	// $ANTLR start "multExpr"
	// D:\\projects\\CodeStory\\Math.g:20:1: multExpr returns [BigDecimal value] : e= unaryExpr ( '*' e= unaryExpr | '/' e= unaryExpr )* ;
	public final BigDecimal multExpr() {
		BigDecimal value = null;


		BigDecimal e =null;

		try {
			// D:\\projects\\CodeStory\\Math.g:21:5: (e= unaryExpr ( '*' e= unaryExpr | '/' e= unaryExpr )* )
			// D:\\projects\\CodeStory\\Math.g:21:9: e= unaryExpr ( '*' e= unaryExpr | '/' e= unaryExpr )*
			{
			pushFollow(FOLLOW_unaryExpr_in_multExpr120);
			e=unaryExpr();
			state._fsp--;

			value = e;
			// D:\\projects\\CodeStory\\Math.g:22:9: ( '*' e= unaryExpr | '/' e= unaryExpr )*
			loop2:
			while (true) {
				int alt2=3;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==7) ) {
					alt2=1;
				}
				else if ( (LA2_0==10) ) {
					alt2=2;
				}

				switch (alt2) {
				case 1 :
					// D:\\projects\\CodeStory\\Math.g:22:10: '*' e= unaryExpr
					{
					match(input,7,FOLLOW_7_in_multExpr134); 
					pushFollow(FOLLOW_unaryExpr_in_multExpr140);
					e=unaryExpr();
					state._fsp--;

					value = value.multiply(e);
					}
					break;
				case 2 :
					// D:\\projects\\CodeStory\\Math.g:23:10: '/' e= unaryExpr
					{
					match(input,10,FOLLOW_10_in_multExpr153); 
					pushFollow(FOLLOW_unaryExpr_in_multExpr159);
					e=unaryExpr();
					state._fsp--;

					value = value.divide(e, 10, RoundingMode.HALF_UP);
					}
					break;

				default :
					break loop2;
				}
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "multExpr"



	// $ANTLR start "unaryExpr"
	// D:\\projects\\CodeStory\\Math.g:27:1: unaryExpr returns [BigDecimal value] : (e= atom | '-' e= atom );
	public final BigDecimal unaryExpr() {
		BigDecimal value = null;


		BigDecimal e =null;

		try {
			// D:\\projects\\CodeStory\\Math.g:28:5: (e= atom | '-' e= atom )
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( ((LA3_0 >= FLOAT && LA3_0 <= 5)) ) {
				alt3=1;
			}
			else if ( (LA3_0==9) ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// D:\\projects\\CodeStory\\Math.g:28:9: e= atom
					{
					pushFollow(FOLLOW_atom_in_unaryExpr200);
					e=atom();
					state._fsp--;

					value = e;
					}
					break;
				case 2 :
					// D:\\projects\\CodeStory\\Math.g:29:9: '-' e= atom
					{
					match(input,9,FOLLOW_9_in_unaryExpr212); 
					pushFollow(FOLLOW_atom_in_unaryExpr218);
					e=atom();
					state._fsp--;

					value = e.negate();
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "unaryExpr"



	// $ANTLR start "atom"
	// D:\\projects\\CodeStory\\Math.g:32:1: atom returns [BigDecimal value] : ( FLOAT | '(' expr ')' );
	public final BigDecimal atom() {
		BigDecimal value = null;


		Token FLOAT1=null;
		BigDecimal expr2 =null;

		try {
			// D:\\projects\\CodeStory\\Math.g:33:5: ( FLOAT | '(' expr ')' )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==FLOAT) ) {
				alt4=1;
			}
			else if ( (LA4_0==5) ) {
				alt4=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
				case 1 :
					// D:\\projects\\CodeStory\\Math.g:33:9: FLOAT
					{
					FLOAT1=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_atom243); 
					value = new BigDecimal((FLOAT1!=null?FLOAT1.getText():null));
					}
					break;
				case 2 :
					// D:\\projects\\CodeStory\\Math.g:34:9: '(' expr ')'
					{
					match(input,5,FOLLOW_5_in_atom255); 
					pushFollow(FOLLOW_expr_in_atom257);
					expr2=expr();
					state._fsp--;

					match(input,6,FOLLOW_6_in_atom259); 
					value = expr2;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return value;
	}
	// $ANTLR end "atom"

	// Delegated rules



	public static final BitSet FOLLOW_multExpr_in_expr36 = new BitSet(new long[]{0x0000000000000302L});
	public static final BitSet FOLLOW_8_in_expr52 = new BitSet(new long[]{0x0000000000000230L});
	public static final BitSet FOLLOW_multExpr_in_expr58 = new BitSet(new long[]{0x0000000000000302L});
	public static final BitSet FOLLOW_9_in_expr74 = new BitSet(new long[]{0x0000000000000230L});
	public static final BitSet FOLLOW_multExpr_in_expr80 = new BitSet(new long[]{0x0000000000000302L});
	public static final BitSet FOLLOW_unaryExpr_in_multExpr120 = new BitSet(new long[]{0x0000000000000482L});
	public static final BitSet FOLLOW_7_in_multExpr134 = new BitSet(new long[]{0x0000000000000230L});
	public static final BitSet FOLLOW_unaryExpr_in_multExpr140 = new BitSet(new long[]{0x0000000000000482L});
	public static final BitSet FOLLOW_10_in_multExpr153 = new BitSet(new long[]{0x0000000000000230L});
	public static final BitSet FOLLOW_unaryExpr_in_multExpr159 = new BitSet(new long[]{0x0000000000000482L});
	public static final BitSet FOLLOW_atom_in_unaryExpr200 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_9_in_unaryExpr212 = new BitSet(new long[]{0x0000000000000030L});
	public static final BitSet FOLLOW_atom_in_unaryExpr218 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_atom243 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_5_in_atom255 = new BitSet(new long[]{0x0000000000000230L});
	public static final BitSet FOLLOW_expr_in_atom257 = new BitSet(new long[]{0x0000000000000040L});
	public static final BitSet FOLLOW_6_in_atom259 = new BitSet(new long[]{0x0000000000000002L});
}

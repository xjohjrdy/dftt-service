package com.atao.dftt.util.hstt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	public class AESDecodeException extends Exception {
		public AESDecodeException(Throwable arg1) {
			super(arg1);
		}

		public AESDecodeException(String arg1) {
			super(arg1);
		}
	}

	static boolean a = false;
	private static final Object b;

	static {
		b = "AES";
	}

	public static String a(byte[] arg5) {
		StringBuffer v0 = new StringBuffer();
		int v1;
		for (v1 = 0; v1 < arg5.length; ++v1) {
			String v2 = Integer.toHexString(arg5[v1] & 255);
			if (v2.length() == 1) {
				v2 = '0' + v2;
			}

			v0.append(v2.toUpperCase());
		}

		return v0.toString();
	}

	public static byte[] a(String arg6) {
		if (arg6.length() < 1) {
			return null;
		}

		byte[] v0 = new byte[arg6.length() / 2];
		int v1;
		for (v1 = 0; v1 < arg6.length() / 2; ++v1) {
			int v2 = v1 * 2;
			int v3 = v2 + 1;
			v0[v1] = ((byte) (Integer.parseInt(arg6.substring(v2, v3), 16) * 16
					+ Integer.parseInt(arg6.substring(v3, v2 + 2), 16)));
		}

		return v0;
	}

	private static byte[] a(String arg2, String arg3) {
		try {
			SecretKeySpec v0 = new SecretKeySpec(AES.d(arg3), "AES");
			Cipher v3 = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] v2_6 = arg2.getBytes("utf-8");
			v3.init(1, ((Key) v0));
			return v3.doFinal(v2_6);
		} catch (BadPaddingException v2) {
			v2.printStackTrace();
		} catch (IllegalBlockSizeException v2_1) {
			v2_1.printStackTrace();
		} catch (UnsupportedEncodingException v2_2) {
			v2_2.printStackTrace();
		} catch (InvalidKeyException v2_3) {
			v2_3.printStackTrace();
		} catch (NoSuchPaddingException v2_4) {
			v2_4.printStackTrace();
		} catch (NoSuchAlgorithmException v2_5) {
			v2_5.printStackTrace();
		}

		return null;
	}

	private static byte[] a(byte[] arg2, String arg3) {
		try {
			SecretKeySpec v0 = new SecretKeySpec(AES.d(arg3), "AES");
			Cipher v3 = Cipher.getInstance("AES/ECB/PKCS5Padding");
			v3.init(2, ((Key) v0));
			return v3.doFinal(arg2);
		} catch (BadPaddingException v2) {
			v2.printStackTrace();
		} catch (IllegalBlockSizeException v2_1) {
			v2_1.printStackTrace();
		} catch (InvalidKeyException v2_2) {
			v2_2.printStackTrace();
		} catch (NoSuchPaddingException v2_3) {
			v2_3.printStackTrace();
		} catch (NoSuchAlgorithmException v2_4) {
			v2_4.printStackTrace();
		}

		return null;
	}

	public static String b(String arg1) {
		return AES.a(AES.a(arg1, "382d2402cd6d7191"));
	}

	public static String c(String arg1) {
		try {
			return new String(AES.a(AES.a(arg1), "382d2402cd6d7191"));
		} catch (Exception v1) {
		}
		return null;
	}

	private static byte[] d(String arg0) {
		if (arg0 != null) {
			return arg0.getBytes();
		}

		return new byte[24];
	}
	
	public static void main(String[] args) {
		String s = AES.c("0DCE03DDEC133CE3DE13E897BE664C3614D0FC7A250B430CB08FAC1EAE253E594066D778DDEE295D9E62BBD7553F4D0D38196A92BCB2D4BC902E942D384F77980A52F643EBE3415809852D75642EC2E1B048C81738AC8D6C71123995A35226ABB7203C298D7FE715622FAEF4CF1C79929A9034518D4D561BD38389F7860EE47C5562224EB548128CDE89E336AB56332257FAE83ECAB98B888541A58B410512FE10CF70F8F979087533A23108214E70C005391CD7741C10CC924B7E6D6E7F3F78392711AB5FB12D3636EFF8895BE465F2895771797FABFF73571B41A222A967C6876B9C26307B81DD60DAF443A55FF26A88015FC412B1412468DBE0946D942807864D9F782645B96373D5F001FFD58C8E99E13E91EDAF27057B656A38343C6F243F7187C9E85C080CA8BD36EE4ECBF64492F05B07C4AA197345DC254F8271A5B571E3EAD0CAB43739E3FED1C38A0C9F341901A6B71E53997D7EE28FB9663470ECEE4C7E7647A685D88B64218C6BD24A45963C1BFAE5E1B1CC8800736E6B5C3DE4C2AB55E21D97A5B7158BD12236257E0B685BE40A7200CA4FD53E191BDFDFA0903ECDE48855CEEC38D9B6C4BACFC849B4348C91A915CE1A21BCEA2A690E2DD7CEA4FABD1E405DA38F77488C46BD0D2BF2C4B3BFC6848B792A5C5F72C502DC61BC433EDE56332F2EF22593C603566CD6997D4413DBFB996B1B1015FD49A751380687E21E45F019DD8F1304CE67EA03D63E70A68D361E5381E8F49C4F7F03F2E11AC04CA7B2694AD159E1A2091D106D34E2E3698180865DFCDDCFD076DBF260CDA88E549D6138761230F806F7397C92E57212FEA9F8C66403B4631C26280DEB9F15480D491908876B32032BD47E0F915CEB81A7C3C0CAF0BA7754A2E30B57CE75435D9E5303AB2EA1E699320CC4592FD5F25321085C3314E1DB1182A284A9D392731D85A5B9B39B491ECA24B939554F34291D4B421F15FD7291F0D18A573EA70E281B442AEA8923F6A34B7FCAAA59043F9E70B3E19D4781459A139714D3026A6DA1E2A6A72A6463E123484ED40C54814F65BB5F08005C3714C0DFDF4E5793F6266D4FD065D4C1199C9E0872FD8548A04E6FC8C92B7A7FCE98ED853D556DF6805F1F609D72FDE887E23EDE483A278E545E70EB03839B63E08AE025EFAB0A19F5F0F727CB193F4E1D51A94B25356663CB33F24FDC818231A707FB1A6A4FB56FD1903BE48E8DE71279D979FC84C42730507C54C25AAABDE42FEB03AB15C332FC3116D6CCA885B4B447089AC7819BF03DA30377772A3D932542EA5D5DDD0383B13E69D6439CB520F6B4E6C372AA9C093DA2F3770F87A5949101B2EE6C7B43F02B8D9498A8345378C83CF1AECF201C592D18C10D24FF5A003383EB7C447F2E6E828E66952E0EFA7B0BF0AB598FA9ED75182693CF806F30CCD5D31C738FF2C2F5F336025E7461A4A152C9E3DE8C351BCEFC8331DE89BFECC873F85F13768C53D6724F5F3A076AE9F8D64B0418E3064BAD8BA84E536414066A07BDAAEBBF20BEA894E382AE72B5D0029D080487BC80A814175D56FC720D269844FE388AA540F3D3472B3857A3829E5E74C21E893021E0484418B09B2857137C4AC5289E33954ACC5C85F28A44F2D0E786FB9B44FF258B922D1116CF15DB35A9F88A5805D603229AAB6D3C9CE38EAA7EBA28A7515B9B5C0BEF55F83DFEAEF223C76355B549A2FF31FFFE0AB5A18DE912C2BD81CF7CDE1F8BE8C21881A5B2B01AF3F33FC6FBC4FDA102D573ECB15117528BE2D93CA351444C7659F44EC60894DEAA866F1E6AD5DE39230DB77B89C2EC3E0E49D80FBA22C459D94FDA5E132AE00E928E19D7D763B9489D5B11CEA6088CD664532FABE8FBBB68BBA32A9FA1FE148C5E56B0804999C0D95608EF940DB8DFD35B2176566223807B797A9CDFE227CF319140176420ED242DB20508B95DFF4FC268417803088DD8F58EDE71A506C90E25AC2A1431A2CE0C708BD876D55926D4D41BA5C286BB0E5BB723BD42D32D11221EB25F7B1B6482F825B3F971E67B95234DFE61CC43FDBD76C390B2653C7E84401A0FFE5746F2A013839BDF273F8863F74F40ECFAB23BF2813B99826F8EDF2D6F4B2293275BBB8BB0686E73F36885416D86D65CDA4F48CED122623DE99FE0C7D41E2E590112AFBC977602ECEAA45F3771155FD22C1A150C90EEBB064615DE2E7DAC8CD872E00649EA01E4C13D444F502E716C193A4F0C0843A39AC7BD2DEA7F4D45091C401F740789EF22490F41C92845884DD40BCEC01A59EBF52186FED5B2E49BFBF415AD183F63A7C2AA77AF04840D0FBFC67BE0374F2FAD0864235BB7CFCD8FBC3605C1C4DB073EF97FC4926972A4176ED6EB376531AF4DCA0E16061EBB448C003B6E250444BC50EEE9D70A6BEA561A2CE4ED2389626325CC5163273692D21ECFF9484A579F7A4E1C4269CD0A32AB1CA5AC2B13108A4AE97EFE2899ED15654F5D6263DA28F52885C5047BBE867DF139D99FEE2EF30564B7FFA1BCB39BB7BA5309C149A1AF6C1E2007D8931BA9E3D172E517D457BAB0291B26C52C9D74A554E4DCE45113BE3B66B18D53CBB30D5A34951B785F1B5C8477342BAB815837DD74BB32CA9C5ECE68BF53CA7FD2B6EC9D3E92963310FD2466A1C21EE5C7F6FADAD74B1BFF34A88461919A46778597C1080535037F36523738876C4D915AC78153435E1E8184A7A2F8F98765C2D2B5645D017AB1DAF598649E2CF4190D967FC23F8B9B3E91B863B048A1F4264043B00F0D5BC3BAA1104BEC337A965B7B0A2ED01D397BFCC627F16A2722662903B2F8240E277EB722B5EBCE15F5E0F4C38A85282702F4E003FFE98D5F9B17BA4F16A09975C3E918CD7B67D15621B2E35EEDB0551EF41E00E84AFDCDBEB3E7D6631B6C0FE34B1BF412D0294B7B440C4882CE4F8CE9580354DFF57263CD157AD657B48358C38D3C83DB8494062233E12D51C896A756D62F26C15BFC4AF8ED2708F978841F34751DC7AA2CB7B2EAB59F4BA71379749DC691763BCE4511D2D793BD39C60C8835B4A07CB3A914CFE457A3694A2ADA5262C5524713A368ED5938C842325877D61D6D3FABD97B05CB489BEA228B0F0DE0374D2D353AB49C052BEA16867C0030A929AB2E02BF0405487665FD4B2352DAE40E5681F40B6F11BC27356A893CA7C948A90502992B3AE9C59E3EBB2A54AEB5678BFB83870637DC66CF236FA899DBB843F5C27592A148E13768A24669E51E5A62E622EB02673C95BA7B9AD7411340ACDC52CCDAB3D3D006E6F8825609B282B94B2FB54BF24A732FD4F0631D03BF478C8EBBE0AC803EDD35DAA98094E674EC606DAC6658DE3ED0AAB5E414885B480CD115FF014312A19393E5CDDDE89A5E62E221C13F3A09CF649863A259A7DA5DEC6D75642C003F11BF19CB6F3B694AC28DB99040866B7EC3CEE75DEB8FB4CF6CB7AEBB81789E16FE4DEB132816808F6E30ABB5EB5F28C72741A493A9B14C6C9BD5FC1EAC41195BD14EF202C76041DF2B9A12F682CEA318A8F87140CB167B9799BA097D62D92414AB6980C1A14662563A3C157C4629D8D4C9BB72606DF5DC2239537C425CEAE90D78939129C537D774EE2EA8D2F27A4ED78948838C7C030CE52B30FD3B3E7A1351A7BB8D706707724FC779F7EB7643107A90CA57FC077B19C303300F31641AD61D70F4E7F03BCCBE0F4DDCD427F494E5B978627BBEB5A10CB350F91E31B5704B8DA5D1A217E2FAC12E9A97F25DC97526BF8B8AAE170AB66E38D0E8CE4DD179D4BDCFFE3954620A1203A226A43FFB539041CBCC7D9D0C2AA33247CFFEE21C60C3A428323570B836A016C528F369930822CFF76F304E2F8ABBFB9B720F3FB865D934346CC14C0DE1F2105DCF4D15EDA89F3F98E8F752E1C19979C497420E9D32ADDA54132F2C077DCF20B634F3C1886D77FF40ECF08ACF1143F606E405CE16C298CCA1688013A175B1A4F598E2236CB3911D0C966839E86CF84F00D0E1");
		System.out.println(s);
	}
}

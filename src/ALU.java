/**
 * 模拟ALU进行整数和浮点数的四则运算
 * @author 151250149_王雪
 *
 */

public class ALU {
	
	
	public static void main(String[] args){
		ALU alu= new ALU();
		String str=alu.integerDivision("0100", "0010", 8);
		System.out.println(str);
	}
	
	/**
	 * 生成十进制整数的二进制补码表示。<br/>
	 * 例：integerRepresentation("9", 8)
	 * @param number 十进制整数。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制补码表示的长度
	 * @return number的二进制补码表示，长度为length
	 */
	public String integerRepresentation (String number, int length) {
		// TODO YOUR CODE HERE.
		
		int num = Integer.valueOf(number);
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<length;i++){
			sb.insert( 0 , num & 1 );
			num = num >> 1;
		}
		String str=sb.toString();
		
		return str;
	}
	
	/**
	 * 生成十进制浮点数的二进制表示。
	 * 需要考虑 0、反规格化、正负无穷（“+Inf”和“-Inf”）、 NaN等因素，具体借鉴 IEEE 754。
	 * 舍入策略为向0舍入。<br/>
	 * 例：floatRepresentation("11.375", 8, 11)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return number的二进制表示，长度为 1+eLength+sLength。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String floatRepresentation (String number, int eLength, int sLength) {
		// TODO YOUR CODE HERE。
		String str="";
		
		//最大指数（254）
		int maxE=(int)Math.pow(2, eLength)-2;
		//指数减数(127)
		int jianshu=maxE/2;
		//最大尾数
		double maxS=1.0f;
		for(int i=1;i<=sLength;i++){
			maxS=maxS+Math.pow(2,-i);
		}
		
		int index=number.indexOf('.');
		String zs="";
		String xs="0.";
		char fuhao='0';
		if(number.charAt(0)=='-'){
			fuhao='1';
			zs=zs+number.substring(1,index);
			xs=xs+number.substring(index+1);
		}else{
			index=number.indexOf('.');
			zs=zs+number.substring(0,index);
			xs=xs+number.substring(index+1);
		}
		
		
		//处理整数
		int zsNum=Integer.parseInt(zs);
		zs=Integer.toBinaryString(zsNum);

		//处理小数
		String temp="";
		float numOfXs=Float.parseFloat(xs);
		if(numOfXs==0.0f){
			temp="0";
		}else{
			int i=0;
			//乘二取整
			while(numOfXs!=0.0f && i<sLength){
				numOfXs=numOfXs * (2.0f);
				if(numOfXs>=1.0f){
					numOfXs=numOfXs - (1.0f);
					temp=temp+"1";
				}else{
					temp=temp+"0";
				}
				i++;
			}
		}
		
		//尾数
		String s=zs+temp;
		if(s.length()<sLength){
			for(int i=0;i<sLength;i++){
				s=s+"0";
			}
		}
		//指数
		String e="";
		
		//0
		if(! s.contains("1") ){
			return getZero(eLength,sLength); 
		}
		
		//现在小数点的位置
		int posOfPoint=zs.length()-1;
		//第一个1的位置
		int posOfOne=s.indexOf('1');
		//差（1-。）11.11(P:1 O:0 delta:1) 0.001(P:0 O:3 delta:-3)
		int deltaPoint=posOfPoint-posOfOne;
		int eNum=jianshu;
		if(deltaPoint==0){
			//小数点与第一个一位置相同，形式为1.xxx，不用规格化
			for(int i=0;i<eLength;i++){
				e=e+"0";
			}
			s=s.substring(1,sLength);
		}else if(deltaPoint>0){
			//小数点在第一个1之后，形式为11.1111XXX
			if(deltaPoint>=jianshu){
				//>=127,溢出,直接返回无穷
				return getInf(fuhao,eLength,sLength);
			}else{
				//正常情况
				eNum=eNum+deltaPoint;
				s=s.substring(1,sLength+1);
				e=e+integerRepresentation(String.valueOf(eNum),eLength);
			}
		}else{
			if(-deltaPoint>=jianshu){
				//delta>=127，反规格化或下溢为0
				if(-deltaPoint-jianshu<=sLength){
					//差<=尾数长度,指数清零，尾数第n位为1
					int t=-deltaPoint-jianshu;
					s="";
					for(int i=0;i<sLength;i++){
						if(i==t){
							s=s+"1";
						}else{
							s=s+"0";
						}
					}
					for(int i=0;i<eLength;i++){
						e=e+"0";
					}
				}else{
					//差>尾数长度，返回0
					return getZero(eLength,sLength);
				}
			}else{
				//delta<127，正常情况
				eNum=eNum+deltaPoint;
				s=s.substring(posOfOne+1,posOfOne+sLength+1);
				e=e+integerRepresentation(String.valueOf(eNum),eLength);
			}
		}

		str=fuhao+" "+e+" "+s;
		return str;
		
	}
	
	/**
	 * 生成十进制浮点数的IEEE 754表示，要求调用{@link #floatRepresentation(String, int, int) floatRepresentation}实现。<br/>
	 * 例：ieee754("11.375", 32)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制表示的长度，为32或64
	 * @return number的IEEE 754表示，长度为length。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String ieee754 (String number, int length) {
		String ieee="";
		
		if(length==32){
			ieee=ieee+floatRepresentation(number,23,8);
		}else{
			ieee=ieee+floatRepresentation(number,52,11);
		}
		
		return ieee;
	}
	
	/**
	 * 计算二进制补码表示的整数的真值。<br/>
	 * 例：integerTrueValue("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 */
	public String integerTrueValue (String operand) {
		// TODO YOUR CODE HERE.
		String trueValue="";
		int temp=0;
		
		char[] c=operand.toCharArray();
		//判断是否为负数
		if(c[0]=='1'){
			//负数加符号位并取反加一
			trueValue=trueValue+"-";
			int indexOfLastOne=operand.lastIndexOf('1');
			for(int i=0;i<indexOfLastOne;i++){
				if(c[i]=='1'){
					c[i]='0';
				}else{
					c[i]='1';
				}
			}	
		}
		
		for(int i=0;i<c.length;i++){
			temp+=(int) ( Math.pow(2, c.length-1-i) * (c[i]-'0') );
		}
		
		trueValue=trueValue+String.valueOf(temp);
		
		return trueValue;
	}
	
	/**
	 * 计算二进制原码表示的浮点数的真值。<br/>
	 * 例：floatTrueValue("01000001001101100000", 8, 11)
	 * @param operand 二进制表示的操作数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位。正负无穷分别表示为“+Inf”和“-Inf”， NaN表示为“NaN”
	 */
	public String floatTrueValue (String operand, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		String str = "";
		double num=0;
		
		char fuhao=operand.charAt(0);
		char[] zhishu=operand.substring(1,eLength+1).toCharArray();
		char[] weishu=operand.substring(eLength+1).toCharArray();
		
		//负数加-
		if(fuhao=='1'){
			str=str+"-";
		}
		
		//指数全为0：
		String allzero="";
		for(int i=0;i<eLength;i++){
			allzero=allzero+"0";
		}
		if(zhishu.equals(allzero)){
			String temp="";
			for(int i=0;i<sLength;i++){
				temp=temp+"0";
			}
			
			if(weishu.equals(temp)){
				//0
				return "0";
			}else{
				//非规格数
				int firstE=(int)(Math.pow(2, eLength-1)-2);
				int secondE=operand.substring(eLength+1).indexOf('1');
				int e=-firstE-secondE;
				double trueValue=Math.pow(2, e);
				str=str+String.valueOf(trueValue);
				return str;
			}
		}
		
		//指数全为1
		String allone="";
		for(int i=0;i<eLength;i++){
			allone=allone+"1";
		}
		if(zhishu.equals(allone)){
			String temp="";
			for(int i=0;i<sLength;i++){
				temp=temp+"1";
			}

			if(weishu.equals(temp)){
				//正负无穷
				if(fuhao=='0'){
					return "+Inf";
				}else{
					return "-Inf";
				}
			}else{
				//非数值
				return "NaN";
			}
		}

		
		//常规情况
		int zs=0;
		int jianshu=((int)Math.pow(2,eLength)-2)/2;
		for(int i=0;i<eLength;i++){
			zs=zs+(int)(Math.pow(2,eLength-1-i))*(zhishu[i]-'0');
		}
		zs=zs-jianshu;
		double ws=1;
		for(int i=0;i<sLength;i++){
			ws=ws+( 1.0f/ (Math.pow(2,eLength-1-i) ) ) * (weishu[i]-'0');
		}
		num=Math.pow(2, zs)*ws;
		str=str+String.valueOf(num);
		
		return str;
	}
	
	/**
	 * 按位取反操作。<br/>
	 * 例：negation("00001001")
	 * @param operand 二进制表示的操作数
	 * @return operand按位取反的结果
	 */
	public String negation (String operand) {
		// TODO YOUR CODE HERE.
		String neg="";
		for(int i=0;i<operand.length();i++){
			if(operand.charAt(i)=='0'){
				neg=neg+"1";
			}else{
				neg=neg+"0";
			}
		}
		return neg;
	}
	
	/**
	 * 左移操作。<br/>
	 * 例：leftShift("00001001", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 左移的位数
	 * @return operand左移n位的结果
	 */
	public String leftShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		int length=operand.length();
		String res="";
		for(int i=0;i<length;i++){
			if(i+n<length){
				res=res+operand.charAt(i+n);
			}else{
				//右边补零
				res=res+"0";
			}
		}
		return res;
	}
	
	/**
	 * 逻辑右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand逻辑右移n位的结果
	 */
	public String logRightShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		int length=operand.length();
		String res="";
		for(int i=length-1;i>=0;i--){
			if(i>=n){
				res=operand.charAt(i-n)+res;
			}else{
				//左边补零
				res="0"+res;
			}
		}
		return res;
	}
	
	/**
	 * 算术右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand算术右移n位的结果
	 */
	public String ariRightShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		int length=operand.length();
		
		String temp="";
		if(operand.charAt(0)=='1'){
			temp=temp+"1";
		}else{
			temp=temp+"0";
		}
		
		String res="";
		
		for(int i=length-1;i>=0;i--){
			if(i>=n){
				res=operand.charAt(i-n)+res;
			}else{
				//左边补1/0
				res=temp+res;
			}
		}
		return res;
	}
	
	/**
	 * 全加器，对两位以及进位进行加法运算。<br/>
	 * 例：fullAdder('1', '1', '0')
	 * @param x 被加数的某一位，取0或1
	 * @param y 加数的某一位，取0或1
	 * @param c 低位对当前位的进位，取0或1
	 * @return 相加的结果，用长度为2的字符串表示，第1位表示进位，第2位表示和
	 */
	public String fullAdder (char x, char y, char c) {
		// TODO YOUR CODE HERE.
		String str="";
		int xi=x-'0';
		int yi=y-'0';
		int ci=c-'0';
		
		int s=xi^yi^ci;
		int c2=(xi&yi)|(xi&ci)|(yi&ci);
		
		str=str+Integer.toString(c2)+Integer.toString(s);
		return str;
	}
	
	/**
	 * 4位先行进位加法器。要求采用{@link #fullAdder(char, char, char) fullAdder}来实现<br/>
	 * 例：claAdder("1001", "0001", '1')
	 * @param operand1 4位二进制表示的被加数
	 * @param operand2 4位二进制表示的加数
	 * @param c 低位对当前位的进位，取0或1
	 * @return 长度为5的字符串表示的计算结果，其中第1位是最高位进位，后4位是相加结果，其中进位不可以由循环获得
	 */
	public String claAdder (String operand1, String operand2, char c) {
		// TODO YOUR CODE HERE.
		String str="";
		int[] o1=toIntArray(operand1);
		int[] o2=toIntArray(operand2);

		int[] p=new int[4];
		int[] g=new int[4];
		int[] cs=new int[5];
		char[] s=new char[4];
		for(int i=0;i<4;i++){
			p[i]=o1[i]|o2[i];
			g[i]=o1[i]&o2[i];
		}
		cs[4]=c-'0';
		
		//先行进位加法器
		cs[3]=g[3] | (p[3]&cs[4]);
		cs[2]=g[2] | (p[2]&g[3]) | (p[2]&p[3]&cs[4]);
		cs[1]=g[1] | (p[1]&g[2]) | (p[1]&p[2]&g[3]) | (p[1]&p[2]&p[3]&cs[4]);
		cs[0]=g[0] | (p[0]&g[1]) | (p[0]&p[1]&g[2]) | (p[0]&p[1]&p[2]&g[3]) | (p[0]&p[1]&p[2]&p[3]&cs[4]);
		
		for(int i=3;i>=0;i--){
			char jinwei=Integer.toString(cs[i+1]).charAt(0);
			s[i]=( fullAdder(operand1.charAt(i),operand2.charAt(i),jinwei) ).charAt(1);
		}
		
		str=str+Integer.toString(cs[0]);
		for(int i=0;i<4;i++){
			str=str+s[i];
		}
		return str;
	}
	
	/**
	 * 加一器，实现操作数加1的运算。
	 * 需要采用与门、或门、异或门等模拟，
	 * 不可以直接调用{@link #fullAdder(char, char, char) fullAdder}、
	 * {@link #claAdder(String, String, char) claAdder}、
	 * {@link #adder(String, String, char, int) adder}、
	 * {@link #integerAddition(String, String, int) integerAddition}方法。<br/>
	 * 例：oneAdder("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand加1的结果，长度为operand的长度加1，其中第1位指示是否溢出（溢出为1，否则为0），其余位为相加结果
	 */
	public String oneAdder (String operand) {
		// TODO YOUR CODE HERE.
		boolean yichu=false;
		String str="";
		
		int length=operand.length();
		int[] x=toIntArray(operand);
		int[] c=new int[length];
		int[] s=new int[length];
				
		for(int i=length-1;i>=0;i--){
			if(i==length-1){
				s[i]=x[i]^1;
				c[i]=x[i]&1;
			}else{
				s[i]=x[i]^c[i+1];
				c[i]=x[i]&c[i+1];
			}
		}
				
		for(int i=length-1;i>=0;i--){
			str=s[i]+str;
		}
		//判断溢出
		if(c[0]==1&&operand.charAt(0)=='0'){
			yichu=true;
		}
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		
		return str;
	}
	
	/**
	 * 加法器，要求调用{@link #claAdder(String, String, char)}方法实现。<br/>
	 * 例：adder("0100", "0011", ‘0’, 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param c 最低位进位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String adder (String operand1, String operand2, char c, int length) {
		// TODO YOUR CODE HERE.
		boolean yichu=false;
		String str="";
		//寄存器是四的几倍
		int time=length/4;
		
		//补符号位
		operand1=bufuhao(operand1,length);
		operand2=bufuhao(operand2,length);
		
		char c0=c;
		//从最后开始加
		for(int i=time-1;i>=0;i--){
			String o1=operand1.substring(length-4,length);
			String o2=operand2.substring(length-4,length);
			String temp=claAdder(o1,o2,c0);
			str=temp.substring(1)+str;
			c0=temp.charAt(0);
			length-=4;
		}
		
		//判断溢出
		if(operand1.charAt(0)=='1' && operand2.charAt(0)=='1' && c=='0'){
			yichu=true;
		}else if(operand1.charAt(0)=='0' && operand2.charAt(0)=='0' && c=='1'){
			yichu=true;
		}
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		return str;
	}
	
	/**
	 * 整数加法，要求调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerAddition("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String integerAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.;
		return adder(operand1,operand2,'0',length);
	}
	
	/**
	 * 整数减法，可调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerSubtraction("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被减数
	 * @param operand2 二进制补码表示的减数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相减结果
	 */
	public String integerSubtraction (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		operand2=negation(operand2);
		return adder(operand1,operand2,'1',length);
	}
	
	/**
	 * 整数乘法，使用Booth算法实现，可调用{@link #adder(String, String, char, int) adder}等方法。<br/>
	 * 例：integerMultiplication("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被乘数
	 * @param operand2 二进制补码表示的乘数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的相乘结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相乘结果
	 */
	public String integerMultiplication (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str="";
		boolean yichu=false;
		
		//判断得数符号
		int fuhao1=operand1.charAt(0)-'0';
		int fuhao2=operand2.charAt(0)-'0';
		int fuhao=fuhao1^fuhao2;
				
		//初始化product
		String product="";
		for(int i=0;i<length;i++){
			product=product+"0";
		}

		//补符号位
		operand1=bufuhao(operand1,length);
		operand2=bufuhao(operand2,length);

		//Y后补0
		String y=operand2+"0";
		
		int[] yint=toIntArray(y);

		for(int i=length;i>0;i--){
			if(yint[i]-yint[i-1]==1){
				product=adder(product,operand1,'0',length).substring(1);
			}else if(yint[i]-yint[i-1]==-1){
				product=adder(product,negation(operand1),'1',length).substring(1);
			}
			str=product+y;
			str=ariRightShift(str,1);
			product=str.substring(0, length);
			y=str.substring(length);
		}
		
		//判断溢出：
		int index=str.indexOf(fuhao^1)-1;
		if(index<length){
			yichu=true;
		}
		
		str=str.substring(length,2*length);
		
		//判断溢出
		if( (str.charAt(0)-'0') !=fuhao){
			yichu=true;
		}

		System.out.println(str);
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		
		return str;
	}
	
	/**
	 * 整数的不恢复余数除法，可调用{@link #adder(String, String, char, int) adder}等方法实现。<br/>
	 * 例：integerDivision("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被除数
	 * @param operand2 二进制补码表示的除数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为2*length+1的字符串表示的相除结果，其中第1位指示是否溢出（溢出为1，否则为0），其后length位为商，最后length位为余数
	 */
	public String integerDivision (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		//除法不溢出
		String str="0";
		
		operand1 = bufuhao(operand1,length);
		operand2 = bufuhao(operand2,length);
		String yushushang = bufuhao(operand1,length*2);
		
		char c = ' ';
		
		if(operand1.charAt(0)==operand2.charAt(0)){
			String temp = integerSubtraction(yushushang.substring(0, length),operand2,length*2);
			yushushang = temp+yushushang.substring(length);
		}else{
			String temp = integerAddition(yushushang.substring(0,length),operand2,length*2);
			yushushang = temp+yushushang.substring(length);
		}
		
		if(yushushang.charAt(0) == operand2.charAt(0)){
			c='1';
		}else{
			c='0';
		}
		
		for(int i=0;i<length;i++){
			yushushang=leftShift(yushushang,1);
			yushushang = yushushang.substring(0,yushushang.length()-1)+c;
			if(c == '1'){
				String temp = integerSubtraction(yushushang.substring(0, length),operand2,length*2);
				yushushang = temp+yushushang.substring(length);
			}else{
				String temp = integerAddition(yushushang.substring(0,length),operand2,length*2);
				yushushang = temp+yushushang.substring(length);
			}
			
			if(yushushang.charAt(0) == operand2.charAt(0)){
				c='1';
			}else{
				c='0';
			}
		}
		
		String yushu = yushushang.substring(0,length);
		String shang = yushushang.substring(length);
		
		if(operand1.charAt(0)!=yushu.charAt(0)){
			if(operand1.charAt(0)==operand2.charAt(0)){
				String temp = integerAddition(yushu,operand2,length*2);
				yushu = temp.substring(length, length * 2);
			}else{
				String temp = integerSubtraction(yushu,operand2,length*2);
				yushu = temp.substring(length, length * 2);
			}
		}
		
		shang = leftShift(shang,1);
		shang = shang.substring(0, length-1)+c;
		if(operand1.charAt(0)!=operand2.charAt(0)){
			shang = integerAddition(shang,"01",length*2);
			shang = shang.substring(length,length*2);
		}
		
		str=str+shang+yushu;
		
		return str;
	}
		
		/*
		//除法不溢出
		boolean yichu=false;
		
		String shang=operand1;
		String yushu="";
		//String temp="";
		
		int ysFuhao=operand1.charAt(0)-'0';
		int csFuhao=operand2.charAt(0)-'0';
		int sFuhao=ysFuhao;
		
		//补符号
		if(operand1.charAt(0)=='0'){
			yushu=yushu+bufuhao("0",length);
		}else{
			yushu=yushu+bufuhao("1",length);
		}
		shang=bufuhao(shang,length);
		
		//temp=temp+yushu+shang;
		
		for(int i=0;i<length-1;i++){
			String temp=yushu+shang;
			
			if(ysFuhao==csFuhao){
				yushu=adder(yushu,negation(operand2),'1',length).substring(1);
			}else{
				yushu=adder(yushu,operand2,'0',length).substring(1);
			}
			ysFuhao=yushu.charAt(0)-'0';
			
			//补1、补0
			if(ysFuhao==csFuhao){
				shang=shang+"1";
			}else{
				shang=shang+"0";
			}
			
			//左移
			temp=yushu+shang;
			temp=leftShift(temp,1);
			yushu=temp.substring(0,length);
			shang=temp.substring(length,2*length);
			ysFuhao=yushu.charAt(0)-'0';
			sFuhao=shang.charAt(0)-'0';
		}
		
		
		leftShift(shang,1);
		if(sFuhao!=csFuhao){
			shang=adder(shang,"1",'0',length).substring(1);
		}
		if(ysFuhao==csFuhao){
			yushu=adder(yushu,negation(operand2),'1',length).substring(1);
		}else{
			yushu=adder(yushu,operand2,'0',length).substring(1);
		}
		
		str=str+shang+yushu;
		
		//判断溢出
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		*/
		
	
	/**
	 * 带符号整数加法，可以调用{@link #adder(String, String, char, int) adder}等方法，
	 * 但不能直接将操作数转换为补码后使用{@link #integerAddition(String, String, int) integerAddition}、
	 * {@link #integerSubtraction(String, String, int) integerSubtraction}来实现。<br/>
	 * 例：signedAddition("1100", "1011", 8)
	 * @param operand1 二进制原码表示的被加数，其中第1位为符号位
	 * @param operand2 二进制原码表示的加数，其中第1位为符号位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度（不包含符号），当某个操作数的长度小于length时，需要将其长度扩展到length
	 * @return 长度为length+2的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），第2位为符号位，后length位是相加结果
	 */
	public String signedAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str="";
		boolean yichu=false;
		
		int fuhao1=operand1.charAt(0)-'0';
		int fuhao2=operand2.charAt(0)-'0';
		
		char c;
		
		//求绝对值（不用补符号
		String o1=operand1.substring(1);
		String o2=operand2.substring(1);
		
		if(fuhao1==0&&fuhao2==0){
			//均为正数，绝对值相加
			str=str+'0'+integerAddition("0"+o1,"0"+o2,length).substring(1);
			c=adder(o1,o2,'0',length).charAt(0);
			if(c=='1'){
				yichu=true;
			}
		}else if(fuhao1==1&&fuhao2==1){
			//均为负数,绝对值相加
			String temp=integerAddition( "0"+o1,"0"+o2,length).substring(1);
			//temp=adder(temp,o2,'1',length);
			str=str+'1'+temp;
			c=temp.charAt(0);
			if(c=='1'){
				yichu=true;
			}
		}else{
			//一正一负，判断绝对值大小
			String larger="";
			String smaller="";

			if( Integer.parseInt(integerTrueValue("0"+o1)) > Integer.parseInt(integerTrueValue("0"+o2)) ){
				larger=larger+o1;
				smaller=smaller+o2;
			}else if( Integer.parseInt(integerTrueValue("0"+o1)) == Integer.parseInt(integerTrueValue("0"+o2)) ){
				//绝对值相等直接返回零（溢出位符号位均为0）
				str=str+"00"+bufuhao("0",length);
				return str;
			}else{
				larger=larger+o2;
				smaller=smaller+o1;
			}
			//符号位为大数符号
			c=larger.charAt(0);
			str=str+c+adder("0"+larger,negation("0"+smaller),'1',length).substring(1);
		}
		
		
		//判断溢出
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		return str;
	}
	
	/**
	 * 浮点数加法，可调用{@link #signedAddition(String, String, int) signedAddition}等方法实现。<br/>
	 * 例：floatAddition("0 01111110 10100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 二进制表示的被加数
	 * @param operand2 二进制表示的加数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相加结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatAddition (String operand1, String operand2, int eLength, int sLength, int gLength) {
		// TODO YOUR CODE HERE.
		String str="";
		String s="";
		String e="";
		
		int e1=Integer.parseInt(integerTrueValue("0"+operand1.substring(1,eLength+1)) );
		int e2=Integer.parseInt(integerTrueValue("0"+operand2.substring(1,eLength+1)));
		String s1=operand1.substring(eLength+1);
		String s2=operand2.substring(eLength+1);
		char fuhao1=operand1.charAt(0);
		char fuhao2=operand2.charAt(0);
		char fuhao='2';
		if(fuhao1==fuhao2){
			fuhao=fuhao1;
		}
		
		//保护位
		char[] g=new char[gLength];
		
		//对阶，原来阶码小的数的尾数右移|△E|位，其阶码值加上|△E|，设定保护位
		int deltaE=e1-e2;
		if(deltaE==0){
			//e1=e2
			if(fuhao1!=fuhao2){
				int i=0;
				while(fuhao=='2'){
					int temp1=s1.substring(i).indexOf('1');
					int temp2=s2.substring(i).indexOf('1');
					if(temp1<temp2){
						fuhao=fuhao1;
						break;
					}else if(temp2<temp1){
						fuhao=fuhao2;
						break;
					}
					i++;
					if(i==sLength){
						fuhao='0';
					}
				}
			}
			
		}else if(deltaE<0){
			//operand1阶码小
			deltaE=-deltaE;
			
			if(deltaE>=gLength){
				g=s1.substring(eLength+sLength-gLength).toCharArray();
			}else{
				g=s1.substring(eLength+sLength-deltaE).toCharArray();
			}
			
			s1=logRightShift("1"+s1,deltaE);
			e1=e2;
			
			if(fuhao1!=fuhao2){
				fuhao=fuhao2;
			}
			
		}else{
			//operand2阶码小
			
			if(deltaE>=gLength){
				g=s2.substring(eLength+sLength-gLength).toCharArray();
			}else{
				g=s2.substring(eLength+sLength-deltaE).toCharArray();
			}
			
			s2=logRightShift("1"+s2,deltaE);
			e2=e1;
			
			if(fuhao1!=fuhao2){
				fuhao=fuhao1;
			}
	
		}
	
		//计算尾数
		s=s+signedAddition(fuhao1+s1,fuhao2+s2,sLength+1).substring(2);
	
		char yichu=signedAddition(fuhao1+s1,fuhao2+s2,sLength).charAt(0);
		if(yichu=='0'){
			//未溢出
		}else{
			//尾数溢出,结果尾数右移一位，并使阶码的值加1（向右规格化）
			logRightShift(s,1);
			e1++;
		}
		
		//尾数舍入,移掉的最高位为1时则在尾数末位加1;为0时则舍去移掉的数值
		if(g[0]=='1'){
			s=oneAdder(s).substring(1);
		}
		
		if(e1>(int)(Math.pow(2, eLength)-2) ){
			yichu='1';
		}else{
			yichu='0';
		}
		
		e=e+integerRepresentation(String.valueOf(e1),eLength);
		str=str+yichu+fuhao+e+s;
	
		return str;
	}

	/**
	 * 浮点数减法，可调用{@link #floatAddition(String, String, int, int, int) floatAddition}方法实现。<br/>
	 * 例：floatSubtraction("0 01111110 10100000", "001111110 01000000", 8, 8, 8)
	 * @param operand1 二进制表示的被减数
	 * @param operand2 二进制表示的减数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相减结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatSubtraction (String operand1, String operand2, int eLength, int sLength, int gLength) {
		// TODO YOUR CODE HERE.
		//o2符号取反
		if(operand2.charAt(0)=='0'){
			operand2="1"+operand2.substring(1);
		}else{
			operand2="0"+operand2.substring(1);
		}
		
		String str=floatAddition(operand1,operand2,eLength,sLength,gLength);
	
		return str;
	}
	
	/**
	 * 浮点数乘法，可调用{@link #integerMultiplication(String, String, int) integerMultiplication}等方法实现。<br/>
	 * 例：floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被乘数
	 * @param operand2 二进制表示的乘数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatMultiplication (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		//任何乘数为0则直接返回0
		String zero=getZero(eLength,sLength);
		if(operand1.equals(zero) || operand2.equals (zero) ){
			return "0"+zero;
		}
		
		String str="";
		char yichu='0';
		
		//计算符号
		char fuhao1=operand1.charAt(0);
		char fuhao2=operand2.charAt(0);
		char fuhao='0';
		
		if(fuhao1!=fuhao2){
			//符号不同为负
			fuhao='1';
		}
		
		//计算指数
		int eMax=(int)Math.pow(2,eLength)-2;
		String e1=operand1.substring(1,eLength);
		String e2=operand2.substring(1,eLength);
		
		//求指数（相加-127）
		String e=adder(e1,e2,'0',eLength).substring(1);
		e= adder(e,negation(integerRepresentation(String.valueOf(eMax/2),eLength) ),'1',eLength).substring(1);
		
		//判断溢出
		if( Integer.valueOf( integerTrueValue("0"+(adder(e1,e2,'0',eLength).substring(1)) ) ) - eMax >= eMax ){
			//溢出，返回正负无穷
			yichu='1';
			return yichu+getInf(fuhao,eLength,sLength);
		}
		
		//计算尾数
		String s1=operand1.substring(eLength+1);
		String s2=operand2.substring(eLength+1);
		String s="";
		
		s=integerMultiplication("0"+s1,"0"+s2,(sLength+2)*2).substring(2,sLength+2);
		
		str=str+yichu+fuhao+e+s;
		return str;
	}
	
	/**
	 * 浮点数除法，可调用{@link #integerDivision(String, String, int) integerDivision}等方法实现。<br/>
	 * 例：floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被除数
	 * @param operand2 二进制表示的除数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatDivision (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
				//除数为0则直接返回NaN
				String zero=getZero(eLength,sLength);
				if(operand2.equals (zero) ){
					return "NaN";
				}else if(operand1.equals(zero)){
					//被除数为零返回0
					return zero;
				}
				
				String str="";
				char yichu='0';
				
				//计算符号
				char fuhao1=operand1.charAt(0);
				char fuhao2=operand2.charAt(0);
				char fuhao='0';
				
				if(fuhao1!=fuhao2){
					//符号不同为负
					fuhao='1';
				}
				
				//计算指数
				int eMax=(int)Math.pow(2,eLength)-2;
				String e1=operand1.substring(1,eLength);
				String e2=operand2.substring(1, eLength);
				
				//求指数（相减+127）
				String e=adder(e1,e2,'0',eLength).substring(1);
				e= adder(e,integerRepresentation(String.valueOf(eMax/2),eLength),'0',eLength).substring(1);
				
				
				//计算尾数
				String s1=operand1.substring(eLength+1);
				String s2=operand2.substring(eLength+1);
				String s="";
				
				s=integerDivision("0"+s1,"0"+s2,(sLength+2)*2).substring(3,sLength+2);
				
				str=str+yichu+fuhao+e+s;
				return str;
	}
	
	
	
	//将二进制字符串转化为int数组
	public static int[] toIntArray(String operand){
		int[] res=new int[operand.length()];
		for(int i=0;i<operand.length();i++){
			res[i]=operand.charAt(i)-'0';
		}
		return res;
	}
	
	//将int数组转化为二进制字符串
	public static String toString(int[] array){
		String str="";
		for(int i=0;i<array.length;i++){
			if(array[i]==1){
				str=str+"1";
			}else{
				str=str+"0";
			}
		}
		return str;
	}

	//补符号位
	public static String bufuhao(String operand,int needLength){
		String res="";
		int trueLength=operand.length();
		for(int i=0;i<needLength-trueLength;i++){
			if(operand.charAt(0)=='1'){
				res="1"+res;
			}else{
				res="0"+res;
			}
		}
		res=res+operand;
		return res;
	}

	//得到正负无穷
	public static String getInf(char fuhao,int eLength,int sLength){
		String str=fuhao+"";
		for(int i=0;i<eLength;i++){
			str=str+"1";
		}
		for(int i=0;i<sLength;i++){
			str=str+"0";
		}
		return str;
	}

	//得到0
	public static String getZero(int eLength,int sLength){
		String str="";
		
		for(int i=0;i<eLength+sLength;i++){
			str=str+"0";
		}
		
		str="0"+str;
		return str;
	}
}

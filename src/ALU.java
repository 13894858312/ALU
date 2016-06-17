/**
 * ģ��ALU���������͸���������������
 * @author 151250149_��ѩ
 *
 */

public class ALU {
	
	
	public static void main(String[] args){
		ALU alu= new ALU();
		String str=alu.integerDivision("0100", "0010", 8);
		System.out.println(str);
	}
	
	/**
	 * ����ʮ���������Ķ����Ʋ����ʾ��<br/>
	 * ����integerRepresentation("9", 8)
	 * @param number ʮ������������Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param length �����Ʋ����ʾ�ĳ���
	 * @return number�Ķ����Ʋ����ʾ������Ϊlength
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
	 * ����ʮ���Ƹ������Ķ����Ʊ�ʾ��
	 * ��Ҫ���� 0������񻯡����������+Inf���͡�-Inf������ NaN�����أ������� IEEE 754��
	 * �������Ϊ��0���롣<br/>
	 * ����floatRepresentation("11.375", 8, 11)
	 * @param number ʮ���Ƹ�����������С���㡣��Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return number�Ķ����Ʊ�ʾ������Ϊ 1+eLength+sLength���������ң�����Ϊ���š�ָ���������ʾ����β������λ���أ�
	 */
	public String floatRepresentation (String number, int eLength, int sLength) {
		// TODO YOUR CODE HERE��
		String str="";
		
		//���ָ����254��
		int maxE=(int)Math.pow(2, eLength)-2;
		//ָ������(127)
		int jianshu=maxE/2;
		//���β��
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
		
		
		//��������
		int zsNum=Integer.parseInt(zs);
		zs=Integer.toBinaryString(zsNum);

		//����С��
		String temp="";
		float numOfXs=Float.parseFloat(xs);
		if(numOfXs==0.0f){
			temp="0";
		}else{
			int i=0;
			//�˶�ȡ��
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
		
		//β��
		String s=zs+temp;
		if(s.length()<sLength){
			for(int i=0;i<sLength;i++){
				s=s+"0";
			}
		}
		//ָ��
		String e="";
		
		//0
		if(! s.contains("1") ){
			return getZero(eLength,sLength); 
		}
		
		//����С�����λ��
		int posOfPoint=zs.length()-1;
		//��һ��1��λ��
		int posOfOne=s.indexOf('1');
		//�1-����11.11(P:1 O:0 delta:1) 0.001(P:0 O:3 delta:-3)
		int deltaPoint=posOfPoint-posOfOne;
		int eNum=jianshu;
		if(deltaPoint==0){
			//С�������һ��һλ����ͬ����ʽΪ1.xxx�����ù��
			for(int i=0;i<eLength;i++){
				e=e+"0";
			}
			s=s.substring(1,sLength);
		}else if(deltaPoint>0){
			//С�����ڵ�һ��1֮����ʽΪ11.1111XXX
			if(deltaPoint>=jianshu){
				//>=127,���,ֱ�ӷ�������
				return getInf(fuhao,eLength,sLength);
			}else{
				//�������
				eNum=eNum+deltaPoint;
				s=s.substring(1,sLength+1);
				e=e+integerRepresentation(String.valueOf(eNum),eLength);
			}
		}else{
			if(-deltaPoint>=jianshu){
				//delta>=127������񻯻�����Ϊ0
				if(-deltaPoint-jianshu<=sLength){
					//��<=β������,ָ�����㣬β����nλΪ1
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
					//��>β�����ȣ�����0
					return getZero(eLength,sLength);
				}
			}else{
				//delta<127���������
				eNum=eNum+deltaPoint;
				s=s.substring(posOfOne+1,posOfOne+sLength+1);
				e=e+integerRepresentation(String.valueOf(eNum),eLength);
			}
		}

		str=fuhao+" "+e+" "+s;
		return str;
		
	}
	
	/**
	 * ����ʮ���Ƹ�������IEEE 754��ʾ��Ҫ�����{@link #floatRepresentation(String, int, int) floatRepresentation}ʵ�֡�<br/>
	 * ����ieee754("11.375", 32)
	 * @param number ʮ���Ƹ�����������С���㡣��Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param length �����Ʊ�ʾ�ĳ��ȣ�Ϊ32��64
	 * @return number��IEEE 754��ʾ������Ϊlength���������ң�����Ϊ���š�ָ���������ʾ����β������λ���أ�
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
	 * ��������Ʋ����ʾ����������ֵ��<br/>
	 * ����integerTrueValue("00001001")
	 * @param operand �����Ʋ����ʾ�Ĳ�����
	 * @return operand����ֵ����Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 */
	public String integerTrueValue (String operand) {
		// TODO YOUR CODE HERE.
		String trueValue="";
		int temp=0;
		
		char[] c=operand.toCharArray();
		//�ж��Ƿ�Ϊ����
		if(c[0]=='1'){
			//�����ӷ���λ��ȡ����һ
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
	 * ���������ԭ���ʾ�ĸ���������ֵ��<br/>
	 * ����floatTrueValue("01000001001101100000", 8, 11)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return operand����ֵ����Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ����������ֱ��ʾΪ��+Inf���͡�-Inf���� NaN��ʾΪ��NaN��
	 */
	public String floatTrueValue (String operand, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		String str = "";
		double num=0;
		
		char fuhao=operand.charAt(0);
		char[] zhishu=operand.substring(1,eLength+1).toCharArray();
		char[] weishu=operand.substring(eLength+1).toCharArray();
		
		//������-
		if(fuhao=='1'){
			str=str+"-";
		}
		
		//ָ��ȫΪ0��
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
				//�ǹ����
				int firstE=(int)(Math.pow(2, eLength-1)-2);
				int secondE=operand.substring(eLength+1).indexOf('1');
				int e=-firstE-secondE;
				double trueValue=Math.pow(2, e);
				str=str+String.valueOf(trueValue);
				return str;
			}
		}
		
		//ָ��ȫΪ1
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
				//��������
				if(fuhao=='0'){
					return "+Inf";
				}else{
					return "-Inf";
				}
			}else{
				//����ֵ
				return "NaN";
			}
		}

		
		//�������
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
	 * ��λȡ��������<br/>
	 * ����negation("00001001")
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @return operand��λȡ���Ľ��
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
	 * ���Ʋ�����<br/>
	 * ����leftShift("00001001", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand����nλ�Ľ��
	 */
	public String leftShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		int length=operand.length();
		String res="";
		for(int i=0;i<length;i++){
			if(i+n<length){
				res=res+operand.charAt(i+n);
			}else{
				//�ұ߲���
				res=res+"0";
			}
		}
		return res;
	}
	
	/**
	 * �߼����Ʋ�����<br/>
	 * ����logRightShift("11110110", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand�߼�����nλ�Ľ��
	 */
	public String logRightShift (String operand, int n) {
		// TODO YOUR CODE HERE.
		int length=operand.length();
		String res="";
		for(int i=length-1;i>=0;i--){
			if(i>=n){
				res=operand.charAt(i-n)+res;
			}else{
				//��߲���
				res="0"+res;
			}
		}
		return res;
	}
	
	/**
	 * �������Ʋ�����<br/>
	 * ����logRightShift("11110110", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand��������nλ�Ľ��
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
				//��߲�1/0
				res=temp+res;
			}
		}
		return res;
	}
	
	/**
	 * ȫ����������λ�Լ���λ���мӷ����㡣<br/>
	 * ����fullAdder('1', '1', '0')
	 * @param x ��������ĳһλ��ȡ0��1
	 * @param y ������ĳһλ��ȡ0��1
	 * @param c ��λ�Ե�ǰλ�Ľ�λ��ȡ0��1
	 * @return ��ӵĽ�����ó���Ϊ2���ַ�����ʾ����1λ��ʾ��λ����2λ��ʾ��
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
	 * 4λ���н�λ�ӷ�����Ҫ�����{@link #fullAdder(char, char, char) fullAdder}��ʵ��<br/>
	 * ����claAdder("1001", "0001", '1')
	 * @param operand1 4λ�����Ʊ�ʾ�ı�����
	 * @param operand2 4λ�����Ʊ�ʾ�ļ���
	 * @param c ��λ�Ե�ǰλ�Ľ�λ��ȡ0��1
	 * @return ����Ϊ5���ַ�����ʾ�ļ����������е�1λ�����λ��λ����4λ����ӽ�������н�λ��������ѭ�����
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
		
		//���н�λ�ӷ���
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
	 * ��һ����ʵ�ֲ�������1�����㡣
	 * ��Ҫ�������š����š�����ŵ�ģ�⣬
	 * ������ֱ�ӵ���{@link #fullAdder(char, char, char) fullAdder}��
	 * {@link #claAdder(String, String, char) claAdder}��
	 * {@link #adder(String, String, char, int) adder}��
	 * {@link #integerAddition(String, String, int) integerAddition}������<br/>
	 * ����oneAdder("00001001")
	 * @param operand �����Ʋ����ʾ�Ĳ�����
	 * @return operand��1�Ľ��������Ϊoperand�ĳ��ȼ�1�����е�1λָʾ�Ƿ���������Ϊ1������Ϊ0��������λΪ��ӽ��
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
		//�ж����
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
	 * �ӷ�����Ҫ�����{@link #claAdder(String, String, char)}����ʵ�֡�<br/>
	 * ����adder("0100", "0011", ��0��, 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param c ���λ��λ
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����ӽ��
	 */
	public String adder (String operand1, String operand2, char c, int length) {
		// TODO YOUR CODE HERE.
		boolean yichu=false;
		String str="";
		//�Ĵ������ĵļ���
		int time=length/4;
		
		//������λ
		operand1=bufuhao(operand1,length);
		operand2=bufuhao(operand2,length);
		
		char c0=c;
		//�����ʼ��
		for(int i=time-1;i>=0;i--){
			String o1=operand1.substring(length-4,length);
			String o2=operand2.substring(length-4,length);
			String temp=claAdder(o1,o2,c0);
			str=temp.substring(1)+str;
			c0=temp.charAt(0);
			length-=4;
		}
		
		//�ж����
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
	 * �����ӷ���Ҫ�����{@link #adder(String, String, char, int) adder}����ʵ�֡�<br/>
	 * ����integerAddition("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����ӽ��
	 */
	public String integerAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.;
		return adder(operand1,operand2,'0',length);
	}
	
	/**
	 * �����������ɵ���{@link #adder(String, String, char, int) adder}����ʵ�֡�<br/>
	 * ����integerSubtraction("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ��������
	 */
	public String integerSubtraction (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		operand2=negation(operand2);
		return adder(operand1,operand2,'1',length);
	}
	
	/**
	 * �����˷���ʹ��Booth�㷨ʵ�֣��ɵ���{@link #adder(String, String, char, int) adder}�ȷ�����<br/>
	 * ����integerMultiplication("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ĳ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ����˽�������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����˽��
	 */
	public String integerMultiplication (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str="";
		boolean yichu=false;
		
		//�жϵ�������
		int fuhao1=operand1.charAt(0)-'0';
		int fuhao2=operand2.charAt(0)-'0';
		int fuhao=fuhao1^fuhao2;
				
		//��ʼ��product
		String product="";
		for(int i=0;i<length;i++){
			product=product+"0";
		}

		//������λ
		operand1=bufuhao(operand1,length);
		operand2=bufuhao(operand2,length);

		//Y��0
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
		
		//�ж������
		int index=str.indexOf(fuhao^1)-1;
		if(index<length){
			yichu=true;
		}
		
		str=str.substring(length,2*length);
		
		//�ж����
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
	 * �����Ĳ��ָ������������ɵ���{@link #adder(String, String, char, int) adder}�ȷ���ʵ�֡�<br/>
	 * ����integerDivision("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ĳ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊ2*length+1���ַ�����ʾ�������������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0�������lengthλΪ�̣����lengthλΪ����
	 */
	public String integerDivision (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		//���������
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
		//���������
		boolean yichu=false;
		
		String shang=operand1;
		String yushu="";
		//String temp="";
		
		int ysFuhao=operand1.charAt(0)-'0';
		int csFuhao=operand2.charAt(0)-'0';
		int sFuhao=ysFuhao;
		
		//������
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
			
			//��1����0
			if(ysFuhao==csFuhao){
				shang=shang+"1";
			}else{
				shang=shang+"0";
			}
			
			//����
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
		
		//�ж����
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		*/
		
	
	/**
	 * �����������ӷ������Ե���{@link #adder(String, String, char, int) adder}�ȷ�����
	 * ������ֱ�ӽ�������ת��Ϊ�����ʹ��{@link #integerAddition(String, String, int) integerAddition}��
	 * {@link #integerSubtraction(String, String, int) integerSubtraction}��ʵ�֡�<br/>
	 * ����signedAddition("1100", "1011", 8)
	 * @param operand1 ������ԭ���ʾ�ı����������е�1λΪ����λ
	 * @param operand2 ������ԭ���ʾ�ļ��������е�1λΪ����λ
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ����������ţ�����ĳ���������ĳ���С��lengthʱ����Ҫ���䳤����չ��length
	 * @return ����Ϊlength+2���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������2λΪ����λ����lengthλ����ӽ��
	 */
	public String signedAddition (String operand1, String operand2, int length) {
		// TODO YOUR CODE HERE.
		String str="";
		boolean yichu=false;
		
		int fuhao1=operand1.charAt(0)-'0';
		int fuhao2=operand2.charAt(0)-'0';
		
		char c;
		
		//�����ֵ�����ò�����
		String o1=operand1.substring(1);
		String o2=operand2.substring(1);
		
		if(fuhao1==0&&fuhao2==0){
			//��Ϊ����������ֵ���
			str=str+'0'+integerAddition("0"+o1,"0"+o2,length).substring(1);
			c=adder(o1,o2,'0',length).charAt(0);
			if(c=='1'){
				yichu=true;
			}
		}else if(fuhao1==1&&fuhao2==1){
			//��Ϊ����,����ֵ���
			String temp=integerAddition( "0"+o1,"0"+o2,length).substring(1);
			//temp=adder(temp,o2,'1',length);
			str=str+'1'+temp;
			c=temp.charAt(0);
			if(c=='1'){
				yichu=true;
			}
		}else{
			//һ��һ�����жϾ���ֵ��С
			String larger="";
			String smaller="";

			if( Integer.parseInt(integerTrueValue("0"+o1)) > Integer.parseInt(integerTrueValue("0"+o2)) ){
				larger=larger+o1;
				smaller=smaller+o2;
			}else if( Integer.parseInt(integerTrueValue("0"+o1)) == Integer.parseInt(integerTrueValue("0"+o2)) ){
				//����ֵ���ֱ�ӷ����㣨���λ����λ��Ϊ0��
				str=str+"00"+bufuhao("0",length);
				return str;
			}else{
				larger=larger+o2;
				smaller=smaller+o1;
			}
			//����λΪ��������
			c=larger.charAt(0);
			str=str+c+adder("0"+larger,negation("0"+smaller),'1',length).substring(1);
		}
		
		
		//�ж����
		if(yichu){
			str="1"+str;
		}else{
			str="0"+str;
		}
		return str;
	}
	
	/**
	 * �������ӷ����ɵ���{@link #signedAddition(String, String, int) signedAddition}�ȷ���ʵ�֡�<br/>
	 * ����floatAddition("0 01111110 10100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ļ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param gLength ����λ�ĳ���
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����ӽ�������е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
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
		
		//����λ
		char[] g=new char[gLength];
		
		//�Խף�ԭ������С������β������|��E|λ�������ֵ����|��E|���趨����λ
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
			//operand1����С
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
			//operand2����С
			
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
	
		//����β��
		s=s+signedAddition(fuhao1+s1,fuhao2+s2,sLength+1).substring(2);
	
		char yichu=signedAddition(fuhao1+s1,fuhao2+s2,sLength).charAt(0);
		if(yichu=='0'){
			//δ���
		}else{
			//β�����,���β������һλ����ʹ�����ֵ��1�����ҹ�񻯣�
			logRightShift(s,1);
			e1++;
		}
		
		//β������,�Ƶ������λΪ1ʱ����β��ĩλ��1;Ϊ0ʱ����ȥ�Ƶ�����ֵ
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
	 * �������������ɵ���{@link #floatAddition(String, String, int, int, int) floatAddition}����ʵ�֡�<br/>
	 * ����floatSubtraction("0 01111110 10100000", "001111110 01000000", 8, 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ļ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param gLength ����λ�ĳ���
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ�������������е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatSubtraction (String operand1, String operand2, int eLength, int sLength, int gLength) {
		// TODO YOUR CODE HERE.
		//o2����ȡ��
		if(operand2.charAt(0)=='0'){
			operand2="1"+operand2.substring(1);
		}else{
			operand2="0"+operand2.substring(1);
		}
		
		String str=floatAddition(operand1,operand2,eLength,sLength,gLength);
	
		return str;
	}
	
	/**
	 * �������˷����ɵ���{@link #integerMultiplication(String, String, int) integerMultiplication}�ȷ���ʵ�֡�<br/>
	 * ����floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ĳ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����˽��,���е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatMultiplication (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
		//�κγ���Ϊ0��ֱ�ӷ���0
		String zero=getZero(eLength,sLength);
		if(operand1.equals(zero) || operand2.equals (zero) ){
			return "0"+zero;
		}
		
		String str="";
		char yichu='0';
		
		//�������
		char fuhao1=operand1.charAt(0);
		char fuhao2=operand2.charAt(0);
		char fuhao='0';
		
		if(fuhao1!=fuhao2){
			//���Ų�ͬΪ��
			fuhao='1';
		}
		
		//����ָ��
		int eMax=(int)Math.pow(2,eLength)-2;
		String e1=operand1.substring(1,eLength);
		String e2=operand2.substring(1,eLength);
		
		//��ָ�������-127��
		String e=adder(e1,e2,'0',eLength).substring(1);
		e= adder(e,negation(integerRepresentation(String.valueOf(eMax/2),eLength) ),'1',eLength).substring(1);
		
		//�ж����
		if( Integer.valueOf( integerTrueValue("0"+(adder(e1,e2,'0',eLength).substring(1)) ) ) - eMax >= eMax ){
			//�����������������
			yichu='1';
			return yichu+getInf(fuhao,eLength,sLength);
		}
		
		//����β��
		String s1=operand1.substring(eLength+1);
		String s2=operand2.substring(eLength+1);
		String s="";
		
		s=integerMultiplication("0"+s1,"0"+s2,(sLength+2)*2).substring(2,sLength+2);
		
		str=str+yichu+fuhao+e+s;
		return str;
	}
	
	/**
	 * �������������ɵ���{@link #integerDivision(String, String, int) integerDivision}�ȷ���ʵ�֡�<br/>
	 * ����floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ĳ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����˽��,���е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatDivision (String operand1, String operand2, int eLength, int sLength) {
		// TODO YOUR CODE HERE.
				//����Ϊ0��ֱ�ӷ���NaN
				String zero=getZero(eLength,sLength);
				if(operand2.equals (zero) ){
					return "NaN";
				}else if(operand1.equals(zero)){
					//������Ϊ�㷵��0
					return zero;
				}
				
				String str="";
				char yichu='0';
				
				//�������
				char fuhao1=operand1.charAt(0);
				char fuhao2=operand2.charAt(0);
				char fuhao='0';
				
				if(fuhao1!=fuhao2){
					//���Ų�ͬΪ��
					fuhao='1';
				}
				
				//����ָ��
				int eMax=(int)Math.pow(2,eLength)-2;
				String e1=operand1.substring(1,eLength);
				String e2=operand2.substring(1, eLength);
				
				//��ָ�������+127��
				String e=adder(e1,e2,'0',eLength).substring(1);
				e= adder(e,integerRepresentation(String.valueOf(eMax/2),eLength),'0',eLength).substring(1);
				
				
				//����β��
				String s1=operand1.substring(eLength+1);
				String s2=operand2.substring(eLength+1);
				String s="";
				
				s=integerDivision("0"+s1,"0"+s2,(sLength+2)*2).substring(3,sLength+2);
				
				str=str+yichu+fuhao+e+s;
				return str;
	}
	
	
	
	//���������ַ���ת��Ϊint����
	public static int[] toIntArray(String operand){
		int[] res=new int[operand.length()];
		for(int i=0;i<operand.length();i++){
			res[i]=operand.charAt(i)-'0';
		}
		return res;
	}
	
	//��int����ת��Ϊ�������ַ���
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

	//������λ
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

	//�õ���������
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

	//�õ�0
	public static String getZero(int eLength,int sLength){
		String str="";
		
		for(int i=0;i<eLength+sLength;i++){
			str=str+"0";
		}
		
		str="0"+str;
		return str;
	}
}

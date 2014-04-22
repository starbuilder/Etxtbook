
package com.android.mylibrary.bookturn;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;


public class BookPageFactory{
    //private final String TAGLOG = "BOOK";
	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private String m_strCharsetName = "utf8";
	private Bitmap m_book_bg = null;
	private int mWidth;
	private int mHeight;
	private Vector<String> m_lines = new Vector<String>();
	private float m_fontSize = 30;
     float m_fontSize_forMsg = 20;

	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffffffee; // �I���C��
	private int marginWidth = 40; // ���k�P��t���Z��
	
	private int marginHeight = 40; // �W�U�P��t���Z��
	
	private int mLineCount; // �C���i�H��ܪ����
	private float mVisibleHeight; // ø��e���e
	private float mVisibleWidth; // ø��e���e
	private boolean m_isfirstPage,m_islastPage;
	String strPercent = "";
	// private int m_nLineSpaceing = 5;
    private int delay_lineCount = 1;

	private Paint mPaint;
	private Paint mPaint_formsg;
	private String BookName = "";
	public BookPageFactory(int w, int h,int marginWidth,int marginHeight) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		this.marginWidth = marginWidth;
		this.marginHeight = marginHeight;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);//�]�mø���r�������V  	
		mPaint.setColor(m_textColor);
		mPaint_formsg = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint_formsg.setColor(m_textColor);
		setM_fontSize_forMsg(30);
		setM_fontSize(30);// �i��ܪ����
	
	
	}
	
	public void openBook(Context context,String fileName){
		try{
			openBook(getRobotCacheFile(context,fileName).getAbsolutePath());
		}catch(IOException e){
			
		}
	}
	  private File getRobotCacheFile(Context context,String fileName) throws IOException {
	        File cacheFile = new File(context.getCacheDir(), fileName);
	        try {
	            InputStream inputStream = context.getAssets().open(fileName);
	            try {
	                FileOutputStream outputStream = new FileOutputStream(cacheFile);
	                try {
	                    byte[] buf = new byte[1024];
	                    int len;
	                    while ((len = inputStream.read(buf)) > 0) {
	                        outputStream.write(buf, 0, len);
	                    }
	                } finally {
	                    outputStream.close();
	                }
	            } finally {
	                inputStream.close();
	            }
	        } catch (IOException e) {
	            throw new IOException("Could not open robot png", e);
	        }
	        return cacheFile;
	    }
	  @SuppressWarnings("resource")
	public void openBook(String strFilePath){
		  try {
			  book_file = new File(strFilePath);
			  long lLen = book_file.length();
			  m_mbBufLen = (int) lLen;

			  /*
			   * ���s�M�g�������A�ЫةM�ק墨�Ǧ]���Ӥj�ӵL�k��J���s�����C���F���s�M�g���A�A�N�i�H�{�����w�g����Ū�i�F���s�A
			   * �M��⥦���@�ӫD�`�j���ƲըӳX�ݡC�o�ظѨM��k��j�j²�ƭק��󪺥N�X�C 
			   * 
			   * fileChannel.map(FileChannel.MapMode mode, long position, long size)�N���q�D�����ϰ쪽���M�g�줺�s���C���O�A�A��
			   * �������A���O�q��󪺭��Ӧ�m�}�l�M�g���A�M�g���d��S���h�j
			   */
			  FileChannel fc=new RandomAccessFile(book_file, "r").getChannel();

			  //���q�D���iŪ�i�g�n�إߦb���y�����iŪ�g����¦���W  
			  m_mbBuf =fc.map(FileChannel.MapMode.READ_ONLY, 0, lLen);
		  }catch(IOException e){

		  }
	  }

	
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}
	//Ū���W�@�q��
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// �ھڽs�X�榡�P�_����
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		//�@Ū���F�h�֦r��
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			//�N�wŪ�����r�ũ�J�Ʋ�
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // Ū���@�Ӭq��
			m_mbBufEnd += paraBuf.length;//������m�ᲾparaBuf.length
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);//�q�Ldecode���w���s�X�榡�Nbyte[]�ഫ���r�Ŧ�			
				} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			
			//�h���N�r�Ŧꤤ���S��r��
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				//�p��C��i�H��ܦh�֭Ӧr��
				//��q��L
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,null);
				nSize = nSize<=strParagraph.length()?nSize:strParagraph.length();
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);//�I���qnSize�}�l���r�Ŧ�
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			//��e���S��ܧ�
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	public void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			//�Ĥ@��
			m_mbBufBegin = 0;
			m_isfirstPage=true;
			return;
		}else m_isfirstPage=false;
		m_lines.clear();//Removes all elements from this vector, leaving it empty.
		pageUp();
		m_lines = pageDown();
	}
	
	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			return;
		}else m_islastPage=false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas c) {
		if (m_lines.size() == 0)
			m_lines = pageDown();	
	    int textheight = (int) (mPaint.descent() - mPaint.ascent())+1;
		if (m_lines.size() > 0) {	
			c.drawColor(0xffffffee);
			if (m_book_bg != null){
				c.drawBitmap(m_book_bg, 0, 0, null);
			}else{
						c.drawColor(m_backColor);
			}
			int y = 0;
			for (String strLine : m_lines) {
				y += textheight;
				//�q�]x,y�^���бN��rø�����̹�		
				c.drawText(strLine, marginWidth, y, mPaint);
			}
		}
		//�p��ʤ���]���]�A��e���^�î榡��
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
	    strPercent = df.format(fPercent * 100) + "%";
	    mPaint_formsg.setTextSize(m_fontSize_forMsg);
		//�p��999.9%�ҥe�������e��	
		int nPercentWidth = (int) mPaint.measureText(strPercent) + 1;
		mPaint_formsg.setTextAlign(Align.RIGHT);
		 int th = (int) (mPaint_formsg.descent() - mPaint_formsg.ascent());
		c.drawText(strPercent, mWidth, mHeight-marginHeight-th , mPaint_formsg);
		mPaint_formsg.setTextAlign(Align.LEFT);
		int size=mPaint_formsg.breakText(BookName, true, mWidth-nPercentWidth,null);
		if(size<BookName.length())
			BookName = BookName.substring(0,size);
		c.drawText(BookName, 0,mHeight-marginHeight-th, mPaint_formsg);
	}

	public void setBookName(String bookName) {
		BookName = bookName;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}
	 
	public boolean isfirstPage() {
		return m_isfirstPage;
	}
	public boolean islastPage() {
		return m_islastPage;
	}
    
	/**�]�w�s�X*/
	public void setM_strCharsetName(String m_strCharsetName) {
		this.m_strCharsetName = m_strCharsetName;
	}
	public String getM_strCharsetName() {
		return m_strCharsetName;
	}

	/**�]�w��rsize*/
	public void setM_fontSize(float m_fontSize) {
		this.m_fontSize = m_fontSize;
		this.mPaint.setTextSize(m_fontSize);
		FontMetrics fontMetrics = mPaint.getFontMetrics();
	    int textheight = (int) (fontMetrics.descent-fontMetrics.ascent+fontMetrics.leading)+1;
		this.mVisibleWidth = this.mWidth - this.marginWidth * 2;
		this.mVisibleHeight = this.mHeight - this.marginHeight * 2;
		this.mLineCount = (int) (this.mVisibleHeight / textheight)-delay_lineCount;
	}
	public void setDelay_lineCount(int delay_lineCount) {
		this.delay_lineCount = delay_lineCount;
	}
	public int getM_backColor() {
		return m_backColor;
	}
	/**�]�w��rColor*/
	public void setM_textColor(int m_textColor) {
		this.m_textColor = m_textColor;
		mPaint.setColor(m_textColor);
	}	
	public int  getM_textColor() {
		return m_textColor;
	}
	public void setM_fontSize_forMsg(float m_fontSize_forMsg) {
		this.m_fontSize_forMsg = m_fontSize_forMsg;
	}
	/**�]�w�ѭ��I���C��*/
	public void setM_backColor(int m_backColor) {
		this.m_backColor = m_backColor;
	}
	/**�]�w�ѭ����k�Z*/
	public void setMarginWidth(int marginWidth) {
		this.marginWidth = marginWidth;
	}
	/**�]�w�ѭ��W�U���Z*/
	public void setMarginHeight(int marginHeight) {
		this.marginHeight = marginHeight;
	}
	/**�_�l��m*/
	public int getM_mbBufBegin() {
		return m_mbBufBegin;
	}
	/**�_�l��m*/
	public void setM_mbBufBegin(int m_mbBufBegin) {
		this.m_mbBufBegin = m_mbBufBegin;
	}
	/**��e����*/
	public Vector<String> getM_lines() {
		return m_lines;
	}
    /**��e����*/
	public void setM_lines(Vector<String> m_lines) {
		this.m_lines = m_lines;
	}
	/**��e�i��*/
	public String getStrPercent() {
		return strPercent;
	}
	/**��e�i��*/
	public void setStrPercent(String strPercent) {
		this.strPercent = strPercent;
	}
	/**��e������m*/
	public int getM_mbBufEnd() {
		return m_mbBufEnd;
	}
	/**��e������m*/
	public void setM_mbBufEnd(int m_mbBufEnd) {
		this.m_mbBufEnd = m_mbBufEnd;
	}
	public float getM_fontSize() {
		return m_fontSize;
	}
}

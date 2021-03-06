package nishimoto.yoshiken;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField textField;
	private String path;
	private JRadioButton[] radio;
	private static int mode;
	private String firstDirPath = "C:\\AutoBinding";

	private String getDirPath(){
		return firstDirPath;
	}

	public String getPath(){
		return path;
	}

	public void setPath(String path){
		this.path = path;
	}

	public static int getMode(){
		return mode;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		File firdir = new File(getDirPath());
		if(!firdir.exists()){
			firdir.mkdir();
		}

		setTitle("Auto Binding");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 30));

		JButton Go = new JButton("実行");
		Go.addActionListener(this);
		Go.setActionCommand("Go");
		panel.add(Go);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setVgap(16);
		contentPane.add(panel_1, BorderLayout.NORTH);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(textField);
		textField.setColumns(30);

		JButton SelectFile = new JButton("参照");
		SelectFile.addActionListener(this);
		SelectFile.setActionCommand("SelectFile");
		panel_1.add(SelectFile);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);

		radio = new JRadioButton[3];
		radio[0] = new JRadioButton("LEAバインディング", true);
		radio[1] = new JRadioButton("Wang式バインディング(VTOPなし)");

		ButtonGroup group = new ButtonGroup();
		group.add(radio[0]);
		group.add(radio[1]);

		panel_2.add(radio[0]);
		panel_2.add(radio[1]);
	}

	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
		String path_a = null;
		if(cmd.equals("SelectFile")){
			JFileChooser fc = new JFileChooser(getDirPath());
			FileFilter filter1 = new FileNameExtensionFilter("スケジューリング済みのDFG(*.dfg)", "dfg");
			FileFilter filter2 = new FileNameExtensionFilter("DATファイル(*.dat)", "dat");
			fc.addChoosableFileFilter(filter1);
			fc.addChoosableFileFilter(filter2);
			fc.setAcceptAllFileFilterUsed(false);
			int selected = fc.showOpenDialog(this);
			if(selected == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				path_a = file.getAbsolutePath();
				textField.setText(path_a);
			}
		}else if(cmd.equals("Go")){
			String path_b = textField.getText();
			setPath(path_b);
			if(getPath() == null){
				JLabel label1 = new JLabel("ファイル参照から絶対パスを指定してください");
				JOptionPane.showMessageDialog(this, label1);
			}
			else{
				if(radio[0].isSelected()){
					mode = 0;
				}
				else if(radio[1].isSelected()){
					mode = 1;
				}
				goBinding();
			}
		}
	}

	public void goBinding(){
		long start = System.currentTimeMillis();
		String outname = null;
		JFileChooser fc1 = new JFileChooser(getDirPath());
		FileFilter filter3 = new FileNameExtensionFilter("DATファイル(*.dat)", "dat");
		fc1.addChoosableFileFilter(filter3);
		fc1.setAcceptAllFileFilterUsed(false);
		int selected1 = fc1.showSaveDialog(this);
		if(selected1 == JFileChooser.APPROVE_OPTION){
			File file1 = fc1.getSelectedFile();
			outname = file1.getAbsolutePath();
			if(!outname.toString().substring(outname.toString().length() -4).equals(".dat")){
				outname = outname + ".dat";
			}
		}
		FileRead.input(getPath());
		FileRead.dataArrange();

		int[] ei = FileRead.getEdge();
		int[] v1 = FileRead.getVer1();
		int[] v2 = FileRead.getVer2();
		int[] vt = FileRead.getVertex();
		String[] ty = FileRead.getType();
		int[] lf = FileRead.getLife();
		int a = FileRead.getAdd();
		int s = FileRead.getSub();
		int m = FileRead.getMult();
		int d = FileRead.getDiv();

		int[] st;
		int[] ed;
		int[] ch;
		int mt;

		if(mode == 0){
			LifetimeAnalysis.Basic(ei, v1, v2, ei, ty, lf);
			st = LifetimeAnalysis.getStart();
			ed = LifetimeAnalysis.getEnd();
			ch = LifetimeAnalysis.getKab();
			mt = LifetimeAnalysis.getMaxtime();
			ModuleAllocation.Basic(a, s, m, d, vt, ty, lf);
			RegisterAllocation.Basic(ei, st, ed, ch, mt);
			FileWrite.output(outname);
			FileRead.resetRC();
		}
		/*
		else if(mode == 1){
			FindCOs.Basic(v1, v2, ty);
			ArrayList<Integer> co = FindCOs.getCOs();
			ConstructTOPs.Basic(co, vt, ei, ty, lf, a, s, m, d, mt);

			int[] vt1 = null;
			String[] ty1 = null;
			int[] lf1 = null;
			ArrayList<Integer> addver;
			int[] ver1_2 = null;
			int[] ei2 = null;

			int[] st2 = null;
			int[] ed2 = null;

			if(ConstructTOPs.getNewSDFGListener()){
				boolean outsdfg = false;
				if(!outsdfg){
					JLabel label2 = new JLabel("新しいSDFGを作成します");
					JOptionPane.showMessageDialog(this, label2);
					String outname_sdfg = getPath();
					if(getPath().toString().substring(getPath().toString().length() - 4).equals(".dfg")){
						outname_sdfg = getPath().replace(".dfg", "");
					}
					outname_sdfg = outname_sdfg + "_wang.dfg";
					FileWrite.newSDFGOutput(outname_sdfg);
					outsdfg = true;
				}

				ArrayList<Integer> ver = new ArrayList<Integer>();
				addver = ConstructTOPs.getAddVer();
				ArrayList<String> type = new ArrayList<String>();
				ArrayList<String> addtype = ConstructTOPs.getAddType();
				ArrayList<Integer> life = new ArrayList<Integer>();
				ArrayList<Integer> addlife = ConstructTOPs.getAddLife();
				ArrayList<Integer> edge = new ArrayList<Integer>();
				ArrayList<Integer> addedge = ConstructTOPs.getAddEdge();
				ArrayList<Integer> ver1  = new ArrayList<Integer>();
				ArrayList<Integer> addver1 = ConstructTOPs.getAddVer1();
				ArrayList<Integer> addver2 = ConstructTOPs.getAddVer2();

				LifetimeAnalysis.Wang(addedge, addver1, addver2, addtype, addlife);
				st2 = LifetimeAnalysis.getAddStart();
				ed2 = LifetimeAnalysis.getAddEnd();

				for(int k = 0; k < vt.length; k++){
					ver.add(vt[k]);
					type.add(ty[k]);
					life.add(lf[k]);
				}
				ver.addAll(addver);
				type.addAll(addtype);
				life.addAll(addlife);
				vt1 = new int[ver.size()];
				ty1 = new String[type.size()];
				lf1 = new int[life.size()];
				for(int n = 0; n < vt1.length; n++){
					vt1[n] = ver.get(n);
					ty1[n] = type.get(n);
					lf1[n] = life.get(n);
				}
				for(int k = 0; k < ei.length; k++){
					edge.add(ei[k]);
					ver1.add(v1[k]);
				}
				edge.addAll(addedge);
				ver1.addAll(addver1);
				ei2 = new int[edge.size()];
				ver1_2 = new int[ver1.size()];
				for(int n = 0; n < ei2.length; n++){
					ei2[n] = edge.get(n);
					ver1_2[n] = ver1.get(n);
				}
			}

			int[][] atop = ConstructTOPs.getAddTOP();
			int[][] stop = ConstructTOPs.getSubTOP();
			int[][] mtop = ConstructTOPs.getMulTOP();
			int[][] dtop = ConstructTOPs.getDivTOP();
			ArrayList<Integer> top = ConstructTOPs.getTOPEdge();
			if(!ConstructTOPs.getNewSDFGListener()){
				ModuleAllocation.Wang(atop, stop, mtop, dtop, a, s, m, d, vt, ty, lf);
				RegisterAllocation.Wang(top, ei, v1, st, ed, ch, mt);
			}
			else{
				ModuleAllocation.Wang(atop, stop, mtop, dtop, a, s, m, d, vt1, ty1, lf1);
				RegisterAllocation.Wang(top, ei2, ver1_2, st2, ed2, ch, mt);
			}

			FileWrite.output(outname);
			FileRead.resetRC();
		}
		**/
		else if(mode == 1){
			while(true){
				ConstructTOPs.resetCycleChanger();
				ConstructTOPs.resetNewSDFGListener();
				ModuleAllocation.resetInModule();
				FindCOs.Basic(v1, v2, ty);
				ArrayList<Integer> co = FindCOs.getCOs();
				LifetimeAnalysis.Basic(ei, v1, v2, ei, ty, lf);
				st = LifetimeAnalysis.getStart();
				ed = LifetimeAnalysis.getEnd();
				ch = LifetimeAnalysis.getKab();
				mt = LifetimeAnalysis.getMaxtime();
				System.out.println("mt:" + mt);
				ConstructTOPs.Basic(co, vt, ei, ty, lf, a, s, m, d, mt);
				if(ConstructTOPs.getCycleChanger()){
					mt = mt + 1;
				}
				else{
					break;
				}
			}


			int[] vt1 = null;
			String[] ty1 = null;
			int[] lf1 = null;
			ArrayList<Integer> addver;
			int[] ver1_2 = null;
			int[] ei2 = null;

			int[] st2 = null;
			int[] ed2 = null;

			if(ConstructTOPs.getNewSDFGListener()){
				boolean outsdfg = false;
				if(!outsdfg){
					JLabel label2 = new JLabel("新しいSDFGを作成します");
					JOptionPane.showMessageDialog(this, label2);
					String outname_sdfg = getPath();
					if(getPath().toString().substring(getPath().toString().length() - 4).equals(".dfg")){
						outname_sdfg = getPath().replace(".dfg", "");
					}
					outname_sdfg = outname_sdfg + "_wang.dfg";
					FileWrite.newSDFGOutput(outname_sdfg);
					outsdfg = true;
				}

				ArrayList<Integer> ver = new ArrayList<Integer>();
				addver = ConstructTOPs.getAddVer();
				ArrayList<String> type = new ArrayList<String>();
				ArrayList<String> addtype = ConstructTOPs.getAddType();
				ArrayList<Integer> life = new ArrayList<Integer>();
				ArrayList<Integer> addlife = ConstructTOPs.getAddLife();
				ArrayList<Integer> edge = new ArrayList<Integer>();
				ArrayList<Integer> addedge = ConstructTOPs.getAddEdge();
				ArrayList<Integer> ver1  = new ArrayList<Integer>();
				ArrayList<Integer> addver1 = ConstructTOPs.getAddVer1();
				ArrayList<Integer> addver2 = ConstructTOPs.getAddVer2();

				LifetimeAnalysis.Wang(addedge, addver1, addver2, addtype, addlife);
				st2 = LifetimeAnalysis.getAddStart();
				ed2 = LifetimeAnalysis.getAddEnd();

				for(int k = 0; k < vt.length; k++){
					ver.add(vt[k]);
					type.add(ty[k]);
					life.add(lf[k]);
				}
				ver.addAll(addver);
				type.addAll(addtype);
				life.addAll(addlife);
				vt1 = new int[ver.size()];
				ty1 = new String[type.size()];
				lf1 = new int[life.size()];
				for(int n = 0; n < vt1.length; n++){
					vt1[n] = ver.get(n);
					ty1[n] = type.get(n);
					lf1[n] = life.get(n);
				}
				for(int k = 0; k < ei.length; k++){
					edge.add(ei[k]);
					ver1.add(v1[k]);
				}
				edge.addAll(addedge);
				ver1.addAll(addver1);
				ei2 = new int[edge.size()];
				ver1_2 = new int[ver1.size()];
				for(int n = 0; n < ei2.length; n++){
					ei2[n] = edge.get(n);
					ver1_2[n] = ver1.get(n);
				}
			}

			int[][] atop = ConstructTOPs.getAddTOP();
			int[][] stop = ConstructTOPs.getSubTOP();
			int[][] mtop = ConstructTOPs.getMulTOP();
			int[][] dtop = ConstructTOPs.getDivTOP();
			ArrayList<Integer> top = ConstructTOPs.getTOPEdge();
			if(!ConstructTOPs.getNewSDFGListener()){
				ModuleAllocation.Wang(atop, stop, mtop, dtop, a, s, m, d, vt, ty, lf);
				RegisterAllocation.Wang(top, ei, v1, st, ed, ch, mt);
			}
			else{
				ModuleAllocation.Wang(atop, stop, mtop, dtop, a, s, m, d, vt1, ty1, lf1);
				RegisterAllocation.Wang(top, ei2, ver1_2, st2, ed2, ch, mt);
			}
			if(ModuleAllocation.getInModule()){
				JLabel label3 = new JLabel("エラーが発生しました。演算機が不足しています。リソース制約を見直してください。");
				JOptionPane.showMessageDialog(this, label3);
			}
			else{
				FileWrite.output(outname);
				FileRead.resetRC();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start)  + "ms");
	}
}

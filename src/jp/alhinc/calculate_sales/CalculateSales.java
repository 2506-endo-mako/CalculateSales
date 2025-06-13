package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String FILE_NOT_SERIAL_NUMBER = "売上ファイル名が連番になっていません";
	private static final String FILE_MORE_THAN_10_DIGITS = "合計金額が10桁を超えました";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数＝args＝フォルダパス(C:\Users\trainee1425\Desktop\売上集計課題)
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			//コマンドライン引数が1つ設定されていなかった場合は、
			//エラーメッセージをコンソールに表⽰します。
			System.out.println(UNKNOWN_ERROR);
		}
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		//61行目private static boolean readFileを呼びます
		if (!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//全てのファイルを格納したい↓
		File[] files = new File(args[0]).listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣言します。
		//↓リスト型のファイル
		List<File> rcdFiles = new ArrayList<>();

		for (int i = 0; i < files.length; i++) {
			//--↓ファイルの情報
			if (files[i].isFile() && files[i].getName().matches("^[0-9]{8}[.]rcd$")) {

				//対象がファイルであり、「数字8桁.rcd」なのか判定します。
				//上記のコードに差し替えるため下記のコードは//で消す
				//if (files[i].getName().matches("^[0-9]{8}[.]rcd$")) {
				//trueの場合の処理
				//売上ファイルの条件に当てはまったものだけ、List(ArrayList) に追加します。
				rcdFiles.add(files[i]);
			}
		}

		Collections.sort(rcdFiles);

		//比較回数は売上ファイルの数よりも1回少ないため、
		//繰り返し回数は売上ファイルのリストの数よりも1つ小さい数です。
		for (int i = 0; i < rcdFiles.size() - 1; i++) {
			//--------------------------------↓ファイル名
			//配列を取るときは、変数名[0]で取れる
			//listから取るときは変数名.get ※この時点ではまだfile型　なので.getName()してあげる必要がある
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			//--------------------------------↓次のファイル名
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

			//比較する2つのファイル名の先頭から数字の8文字を切り出し、int型に変換します。
			if ((latter - former) != 1) {
				//2つのファイル名の数字を比較して、差が1ではなかったら、
				//エラーメッセージをコンソールに表示します。
				System.out.println(FILE_NOT_SERIAL_NUMBER);
			}
		}

		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		for (int i = 0; i < rcdFiles.size(); i++) {

			BufferedReader br = null;
			try {
				//path…args=ファイルパス
				// fileName…rcdFiles(i番目)の名前
				File file = new File(args[0], rcdFiles.get(i).getName());
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				//支店定義ファイル読み込み(readFileメソッド)を参考に売上ファイルの中身を読み込みます。

				//1行読み込んだものを格納
				String line;

				//保持する用のlistを宣言する
				List<String> filelist = new ArrayList<>();

				// 一行ずつ読み込む(値がnullでない限り、1行ずつ読み込み、lineに入れるを繰り返す)
				while ((line = br.readLine()) != null) {

					//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。
					//どちらもこの後の処理で必要となるため、売上ファイルの中身(line)はListで保持(add)しましょう。
					filelist.add(line);
				}

				//--↓売上金額の中身を入れたリスト
				if (filelist.size() != 2) {
					//売上ファイルの行数が2行ではなかった場合は、
					//エラーメッセージをコンソールに表示します。
					System.out.println(rcdFiles.get(i).getName() + "のフォーマットが不正です");
					return;
				}

				//---↓支店コードを入れたMap---↓支店コード
				if (!branchNames.containsKey(filelist.get(0))) {
					//⽀店情報を保持しているMapに売上ファイルの⽀店コードが存在しなかった場合は、
					//エラーメッセージをコンソールに表示します。
					System.out.println(rcdFiles.get(i).getName() + "の支店コードが不正です");
					return;
				}

				//---↓売上金額
				if (!filelist.get(1).matches("^[0-9]+$")) {
					//売上金額が数字ではなかった場合は、
					//エラーメッセージをコンソールに表示します。
					System.out.println("UNKNOWN_ERROR");
					return;
				}

				//売上ファイルから読み込んだ売上金額をMapに加算していくために、型の変換を行います。
				long fileSale = Long.parseLong(filelist.get(1));

				//読み込んだ売上金額を加算します。(branchSalesはMapのこと)
				Long saleAmount = branchSales.get(filelist.get(0)) + fileSale;

				if (saleAmount >= 10000000000L) {
					//売上金額が11桁以上の場合、エラーメッセージをコンソールに表示します。
					System.out.println(FILE_MORE_THAN_10_DIGITS);
				}

				//加算した売上金額をMapに追加します。
				branchSales.put(filelist.get(0), saleAmount);

			} catch (IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if (br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch (IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			} //finallyの終わり
		} //for文の終わり

		if (!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	//上の方の、if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {から呼ばれます
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames,
			Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			//ファイルを開く
			//path…引数で持ってきた値。中身はファイルパス
			// fileName…引数で持ってきた値。中身は”branch.lst”
			//Mapに追加する2つの情報を putの引数として指定します。
			File file = new File(path, fileName);

			//支店定義ファイルが無かったときのエラー処理
			if (!file.exists()) {
				//⽀店定義ファイルが存在しない場合、コンソールにエラーメッセージを表⽰します。
				System.out.println(FILE_NOT_EXIST);
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while ((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");

				//支店定義ファイルの仕様が違った時のエラー処理　支店コード→items[0]
				if ((items.length != 2) || (!items[0].matches("^[0-9]{3}$"))) {
					//⽀店定義ファイルの仕様が満たされていない場合、
					//エラーメッセージをコンソールに表⽰します。
					System.out.println(FILE_INVALID_FORMAT);
					return false;

				}

				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);

			}

		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			//読み込めなかったと返している
			return false;
		} finally {
			// ファイルを開いている場合
			if (br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames,
			Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		BufferedWriter bw = null;

		try {
			//ファイルを開く
			//path…引数で持ってきた値。中身はファイルパス
			// fileName…引数で持ってきた値。中身は”branch.lst”
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			//                   ↓支店コードを入れたmapのこと
			for (String key : branchNames.keySet()) {
				//keyという変数には、Mapから取得したキーが代入されています。
				//拡張for文で繰り返されているので、1つ目のキーが取得できたら、
				//2つ目の取得...といったように、次々とkeyという変数に上書きされていきます。
				//書く      ↓書き込みたい文字列　　　keyを呼び出すとvalueがついてくる
				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				bw.newLine();

			}
		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if (bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}

		return true;
	}

}

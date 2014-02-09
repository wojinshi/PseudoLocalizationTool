/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.tool;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 *
 * @author Administrator
 */
public class PseudolocalizerConsoleSample {

    /**
     * <pre>
     * ①一个目录选项：-d，带参数值，必须选项
     * ②一个日期选项：-D，带参数值，全写--date，可选项
     * ③一个日期范围选项：-r，带参数值，当-D出现时为必选项，否则该选项无效
     * ④一个文件名前缀选项：-p，带参数值，可以有多个前缀名，以逗号分隔，可选项
     * ⑤一个文件扩展名选项：-s，带参数值，可以有多个扩展名，以逗号分隔，可选项
     * ⑥一个文件大小选项：-S，带参数值，全写--file-size，可选项
     * ⑦一个文件大小阀值选项：-l，带参数值，当-S出现时为必选项，否则该选项无效
     * ⑧一个帮助信息选项：-h，无参数值
     * </pre>.
     */

  
  private Options searchOpts = new Options();
  private CommandLine cl = null;

  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    PseudolocalizerConsoleSample processer = new PseudolocalizerConsoleSample();
    processer.run(args);
    processer.validte();
  }

  /**
   * Instantiates a new search command line processer.
   */
  public PseudolocalizerConsoleSample() {
    String desc = "Specify the directory where search start";
    Option optStartDir = OptionBuilder.withDescription(desc).isRequired(false)
            .hasArgs().withArgName("START_DIRECTORY").create('d');
    searchOpts.addOption(optStartDir);
  }

  /**
   * Set rule for command line parser, run parsing process
   *
   * @param args the args
   */
  private void run(String[] args) {
    setExt();
//    setDate();
//    setDateRange();
//    setPrefix();
//    setSuffix();
//    setSize();
//    setSizeRange();
//    setHelp();
    runProcess(searchOpts, args, new PosixParser());
//    runProcess(searchOpts, args, new GnuParser());
  }

  /**
   * Sets the date.
   */
  public void setDate() {
    String desc = "Specify the file create date time";
    Option optDate = OptionBuilder.withDescription(desc).isRequired(false)
            .hasArgs().withArgName("FILE_CREATE_DATE").withLongOpt("date")
            .create('D');
    searchOpts.addOption(optDate);
  }

  /**
   * Sets the date range.
   */
  public void setDateRange() {
    StringBuffer desc = new StringBuffer(
            "Specify acceptance date range for cutoff date specify by option -d");
    desc.append("if true, older files (at or before the cutoff)");
    desc.append("are accepted, else newer ones (after the cutoff)");
    Option optDateRange = null;

    optDateRange = OptionBuilder.withDescription(desc.toString())
            .isRequired(false).hasArg().withArgName("DATE_RANGE")
            .create('r');
    searchOpts.addOption(optDateRange);
  }

  /**
   * Sets the prefix.
   */
  public void setPrefix() {
    String desc = "Specify the prefix of file, multiple prefixes can be split by comma";
    Option optPrefix = OptionBuilder.withDescription(desc)
            .isRequired(false).hasArgs().withArgName("FILE_PREFIXES")
            .create('p');
    searchOpts.addOption(optPrefix);
  }

  /**
   * Sets the suffix.
   */
  public void setSuffix() {
    String desc = "Specify the suffix of file, multiple suffixes can be split by comma";
    Option optSuffix = OptionBuilder.withDescription(desc)
            .isRequired(false).hasArgs().withArgName("FILE_SUFFIXES")
            .create('s');
    searchOpts.addOption(optSuffix);
  }

  /**
   * Sets the size.
   */
  public void setSize() {
    String desc = "Spcify the file size";
    Option optSize = OptionBuilder.withDescription(desc).isRequired(false)
            .hasArg().withArgName("FILE_SIZE_WITH_LONG_VALUE").withLongOpt(
            "file-size").create('S');
    searchOpts.addOption(optSize);
  }

  /**
   * Sets the size range.
   */
  public void setSizeRange() {
    StringBuffer desc = new StringBuffer(
            "Specify acceptance size threshold for file specify by option -S");
    desc.append("if true, files equal to or larger are accepted,");
    desc.append("otherwise smaller ones (but not equal to)");
    Option optDateRange = null;

    optDateRange = OptionBuilder.withDescription(desc.toString())
            .isRequired(false).hasArg().withArgName("SIZE_THRESHOLD")
            .create('l');
    searchOpts.addOption(optDateRange);
  }

  /**
   * Sets the help.
   */
  public void setHelp() {
    String desc = "Print help message and all options information";
    Option optHelp = OptionBuilder.withDescription(desc).isRequired(false)
            .create('h');
    searchOpts.addOption(optHelp);
  }
  
  private void setExt() {
      String desc = "loads additional classes by the fully qualified class name";
      Option optDate = OptionBuilder.withLongOpt("ext")
              .isRequired(false)
              .withDescription(desc)
              .hasArg(true)
              .withValueSeparator()
              .create('e');      
      searchOpts.addOption(optDate);
    }

  /**
   * Run process.
   *
   * @param opts the opts
   * @param args the args
   * @param parser the parser
   */
  public void runProcess(Options opts, String[] args, CommandLineParser parser) {
    try {
      cl = process(searchOpts, args, parser);
    } catch (ParseException e) {
      System.out.println("Error on compile/parse command: "
              + e.getMessage());
      printHelp(opts);
      System.exit(-1);
    }
    Option[] allOpts = cl.getOptions();
    Option opt = null;
    for (int i = 0; i < allOpts.length; i++) {
      opt = allOpts[i];
      if ("h".equals(opt.getOpt())) {
        printHelp(opts);
        System.exit(0);
      }
      System.out.println("Option name: -" + opt.getOpt()
              + ", and value = " + getOptValues(opt.getOpt(), ","));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see example.io.CommandLineProcesser#process(org.apache.commons.cli.Options,
   *      java.lang.String[], org.apache.commons.cli.CommandLineParser)
   */
  public CommandLine process(Options options, String[] args,
          CommandLineParser parser) throws ParseException {
    return parser.parse(options, args);
  }

  /**
   * Validte required option and optional options
   */
  private void validte() {

    // Validate directory option
    String directory = getOptValue("d");
    if (directory == null) {
      System.out.println("Missing start directory, ignore and exit");
      System.exit(-1);
    }
    // Validate date option
    String date = (getOptValue("D") == null) ? getOptValue("date")
            : getOptValue("D");
    String dateRange = getOptValue("r");
    if (date != null && (dateRange == null)) {
      System.out.println("Missing option -D/--date, exit immediately");
      System.exit(-1);
    } else if (date == null && (dateRange != null)) {
      System.out.println("Date not specified, ignore option -r");
    }
    // Validate size option
    String size = (getOptValue("S") == null) ? getOptValue("file-size")
            : getOptValue("S");
    String sizeRange = getOptValue("l");
    if (size != null && (sizeRange == null)) {
      System.out.println("Missing option -S/--file-size, exit immediately");
      System.exit(-1);
    } else if (size == null && (sizeRange != null)) {
      System.out.println("File size not specified, ignore option -l");
    }
  }

  /**
   * Prints the help.
   *
   * @param options the options
   */
  public void printHelp(Options options) {
    String formatstr = "java example.io.SearchCommandLineProcesser [-h][-d][-D/--date<-r>][-p][-s][-S/--size<-l>]";
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(formatstr, options);
  }

  /*
   * (non-Javadoc)
   * 
   * @see example.io.CommandLineProcesser#getOptValue(java.lang.String)
   */
  public String getOptValue(String opt) {
    return (cl != null) ? cl.getOptionValue(opt) : "";
  }

  /*
   * (non-Javadoc)
   * 
   * @see example.io.CommandLineProcesser#getOptValues(java.lang.String)
   */
  public String[] getOptValues(String opt) {
    return (cl != null) ? cl.getOptionValues(opt) : new String[]{""};
  }

  /*
   * (non-Javadoc)
   * 
   * @see example.io.CommandLineProcesser#getOptValues(java.lang.String,
   *      java.lang.String)
   */
  public String getOptValues(String opt, String valueSeparater) {
    String[] values = getOptValues(opt);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < values.length; i++) {
      sb.append(values[i]).append(valueSeparater);
    }
    return sb.subSequence(0, sb.length() - 1).toString();
  }
}

package life.unwitting.http4j.impl;

import java.util.HashMap;

/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class HttpMime {
    public static final String MIME_APPLICATION_ANDREW_INSET = "application/andrew-inset";
    public static final String MIME_APPLICATION_JSON = "application/json";
    public static final String MIME_APPLICATION_ZIP = "application/zip";
    public static final String MIME_APPLICATION_X_GZIP = "application/x-gzip";
    public static final String MIME_APPLICATION_TGZ = "application/tgz";
    public static final String MIME_APPLICATION_MSWORD = "application/msword";
    public static final String MIME_APPLICATION_MSWORD_2007 = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MIME_APPLICATION_VND_TEXT = "application/vnd.oasis.opendocument.text";
    public static final String MIME_APPLICATION_POSTSCRIPT = "application/postscript";
    public static final String MIME_APPLICATION_PDF = "application/pdf";
    public static final String MIME_APPLICATION_JNLP = "application/jnlp";
    public static final String MIME_APPLICATION_MAC_BINHEX40 = "application/mac-binhex40";
    public static final String MIME_APPLICATION_MAC_COMPACTPRO = "application/mac-compactpro";
    public static final String MIME_APPLICATION_MATHML_XML = "application/mathml+xml";
    public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String MIME_APPLICATION_ODA = "application/oda";
    public static final String MIME_APPLICATION_RDF_XML = "application/rdf+xml";
    public static final String MIME_APPLICATION_JAVA_ARCHIVE = "application/java-archive";
    public static final String MIME_APPLICATION_RDF_SMIL = "application/smil";
    public static final String MIME_APPLICATION_SRGS = "application/srgs";
    public static final String MIME_APPLICATION_SRGS_XML = "application/srgs+xml";
    public static final String MIME_APPLICATION_VND_MIF = "application/vnd.mif";
    public static final String MIME_APPLICATION_VND_MSEXCEL = "application/vnd.ms-excel";
    public static final String MIME_APPLICATION_VND_MSEXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_APPLICATION_VND_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet";
    public static final String MIME_APPLICATION_VND_MSPOWERPOINT = "application/vnd.ms-powerpoint";
    public static final String MIME_APPLICATION_VND_RNREALMEDIA = "application/vnd.rn-realmedia";
    public static final String MIME_APPLICATION_X_BCPIO = "application/x-bcpio";
    public static final String MIME_APPLICATION_X_CDLINK = "application/x-cdlink";
    public static final String MIME_APPLICATION_X_CHESS_PGN = "application/x-chess-pgn";
    public static final String MIME_APPLICATION_X_CPIO = "application/x-cpio";
    public static final String MIME_APPLICATION_X_CSH = "application/x-csh";
    public static final String MIME_APPLICATION_X_DIRECTOR = "application/x-director";
    public static final String MIME_APPLICATION_X_DVI = "application/x-dvi";
    public static final String MIME_APPLICATION_X_FUTURESPLASH = "application/x-futuresplash";
    public static final String MIME_APPLICATION_X_GTAR = "application/x-gtar";
    public static final String MIME_APPLICATION_X_HDF = "application/x-hdf";
    public static final String MIME_APPLICATION_X_JAVASCRIPT = "application/x-javascript";
    public static final String MIME_APPLICATION_X_KOAN = "application/x-koan";
    public static final String MIME_APPLICATION_X_LATEX = "application/x-latex";
    public static final String MIME_APPLICATION_X_NETCDF = "application/x-netcdf";
    public static final String MIME_APPLICATION_X_OGG = "application/x-ogg";
    public static final String MIME_APPLICATION_X_SH = "application/x-sh";
    public static final String MIME_APPLICATION_X_SHAR = "application/x-shar";
    public static final String MIME_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
    public static final String MIME_APPLICATION_X_STUFFIT = "application/x-stuffit";
    public static final String MIME_APPLICATION_X_SV4CPIO = "application/x-sv4cpio";
    public static final String MIME_APPLICATION_X_SV4CRC = "application/x-sv4crc";
    public static final String MIME_APPLICATION_X_TAR = "application/x-tar";
    public static final String MIME_APPLICATION_X_RAR_COMPRESSED = "application/x-rar-compressed";
    public static final String MIME_APPLICATION_X_TCL = "application/x-tcl";
    public static final String MIME_APPLICATION_X_TEX = "application/x-tex";
    public static final String MIME_APPLICATION_X_TEXINFO = "application/x-texinfo";
    public static final String MIME_APPLICATION_X_TROFF = "application/x-troff";
    public static final String MIME_APPLICATION_X_TROFF_MAN = "application/x-troff-man";
    public static final String MIME_APPLICATION_X_TROFF_ME = "application/x-troff-me";
    public static final String MIME_APPLICATION_X_TROFF_MS = "application/x-troff-ms";
    public static final String MIME_APPLICATION_X_USTAR = "application/x-ustar";
    public static final String MIME_APPLICATION_X_WAIS_SOURCE = "application/x-wais-source";
    public static final String MIME_APPLICATION_VND_MOZZILLA_XUL_XML = "application/vnd.mozilla.xul+xml";
    public static final String MIME_APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final String MIME_APPLICATION_XSLT_XML = "application/xslt+xml";
    public static final String MIME_APPLICATION_XML = "application/xml";
    public static final String MIME_APPLICATION_XML_DTD = "application/xml-dtd";
    public static final String MIME_IMAGE_BMP = "image/bmp";
    public static final String MIME_IMAGE_CGM = "image/cgm";
    public static final String MIME_IMAGE_GIF = "image/gif";
    public static final String MIME_IMAGE_IEF = "image/ief";
    public static final String MIME_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_IMAGE_TIFF = "image/tiff";
    public static final String MIME_IMAGE_PNG = "image/png";
    public static final String MIME_IMAGE_SVG_XML = "image/svg+xml";
    public static final String MIME_IMAGE_VND_DJVU = "image/vnd.djvu";
    public static final String MIME_IMAGE_WAP_WBMP = "image/vnd.wap.wbmp";
    public static final String MIME_IMAGE_X_CMU_RASTER = "image/x-cmu-raster";
    public static final String MIME_IMAGE_X_ICON = "image/x-icon";
    public static final String MIME_IMAGE_X_PORTABLE_ANYMAP = "image/x-portable-anymap";
    public static final String MIME_IMAGE_X_PORTABLE_BITMAP = "image/x-portable-bitmap";
    public static final String MIME_IMAGE_X_PORTABLE_GRAYMAP = "image/x-portable-graymap";
    public static final String MIME_IMAGE_X_PORTABLE_PIXMAP = "image/x-portable-pixmap";
    public static final String MIME_IMAGE_X_RGB = "image/x-rgb";
    public static final String MIME_AUDIO_BASIC = "audio/basic";
    public static final String MIME_AUDIO_MIDI = "audio/midi";
    public static final String MIME_AUDIO_MPEG = "audio/mpeg";
    public static final String MIME_AUDIO_X_AIFF = "audio/x-aiff";
    public static final String MIME_AUDIO_X_MPEGURL = "audio/x-mpegurl";
    public static final String MIME_AUDIO_X_PN_REALAUDIO = "audio/x-pn-realaudio";
    public static final String MIME_AUDIO_X_WAV = "audio/x-wav";
    public static final String MIME_CHEMICAL_X_PDB = "chemical/x-pdb";
    public static final String MIME_CHEMICAL_X_XYZ = "chemical/x-xyz";
    public static final String MIME_MODEL_IGES = "model/iges";
    public static final String MIME_MODEL_MESH = "model/mesh";
    public static final String MIME_MODEL_VRLM = "model/vrml";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_RICHTEXT = "text/richtext";
    public static final String MIME_TEXT_RTF = "text/rtf";
    public static final String MIME_TEXT_HTML = "text/html";
    public static final String MIME_TEXT_CALENDAR = "text/calendar";
    public static final String MIME_TEXT_CSS = "text/css";
    public static final String MIME_TEXT_SGML = "text/sgml";
    public static final String MIME_TEXT_TAB_SEPARATED_VALUES = "text/tab-separated-values";
    public static final String MIME_TEXT_VND_WAP_XML = "text/vnd.wap.wml";
    public static final String MIME_TEXT_VND_WAP_WMLSCRIPT = "text/vnd.wap.wmlscript";
    public static final String MIME_TEXT_X_SETEXT = "text/x-setext";
    public static final String MIME_TEXT_X_COMPONENT = "text/x-component";
    public static final String MIME_VIDEO_QUICKTIME = "video/quicktime";
    public static final String MIME_VIDEO_MPEG = "video/mpeg";
    public static final String MIME_VIDEO_VND_MPEGURL = "video/vnd.mpegurl";
    public static final String MIME_VIDEO_X_MSVIDEO = "video/x-msvideo";
    public static final String MIME_VIDEO_X_MS_WMV = "video/x-ms-wmv";
    public static final String MIME_VIDEO_X_SGI_MOVIE = "video/x-sgi-movie";
    public static final String MIME_X_CONFERENCE_X_COOLTALK = "x-conference/x-cooltalk";
    public static final String MIME_XML = "text/xml; charset=utf-8";

    private static final HashMap<String, String> mimeTypeMapping;
    private static final HashMap<String, String> extMapping;

    static {
        mimeTypeMapping = new HashMap<String, String>(200) {
            private void set(String key, String value) {
                if (put(key, value) != null) {
                    throw new IllegalArgumentException("Duplicated extension: " + key);
                }
            }

            {
                set(".xul", MIME_APPLICATION_VND_MOZZILLA_XUL_XML);
                set(".json", MIME_APPLICATION_JSON);
                set(".ice", MIME_X_CONFERENCE_X_COOLTALK);
                set(".movie", MIME_VIDEO_X_SGI_MOVIE);
                set(".avi", MIME_VIDEO_X_MSVIDEO);
                set(".wmv", MIME_VIDEO_X_MS_WMV);
                set(".m4u", MIME_VIDEO_VND_MPEGURL);
                set(".mxu", MIME_VIDEO_VND_MPEGURL);
                set(".htc", MIME_TEXT_X_COMPONENT);
                set(".etx", MIME_TEXT_X_SETEXT);
                set(".wmls", MIME_TEXT_VND_WAP_WMLSCRIPT);
                set(".wml", MIME_TEXT_VND_WAP_XML);
                set(".tsv", MIME_TEXT_TAB_SEPARATED_VALUES);
                set(".sgm", MIME_TEXT_SGML);
                set(".sgml", MIME_TEXT_SGML);
                set(".css", MIME_TEXT_CSS);
                set(".ifb", MIME_TEXT_CALENDAR);
                set(".ics", MIME_TEXT_CALENDAR);
                set(".wrl", MIME_MODEL_VRLM);
                set(".vrlm", MIME_MODEL_VRLM);
                set(".silo", MIME_MODEL_MESH);
                set(".mesh", MIME_MODEL_MESH);
                set(".msh", MIME_MODEL_MESH);
                set(".iges", MIME_MODEL_IGES);
                set(".igs", MIME_MODEL_IGES);
                set(".rgb", MIME_IMAGE_X_RGB);
                set(".ppm", MIME_IMAGE_X_PORTABLE_PIXMAP);
                set(".pgm", MIME_IMAGE_X_PORTABLE_GRAYMAP);
                set(".pbm", MIME_IMAGE_X_PORTABLE_BITMAP);
                set(".pnm", MIME_IMAGE_X_PORTABLE_ANYMAP);
                set(".ico", MIME_IMAGE_X_ICON);
                set(".ras", MIME_IMAGE_X_CMU_RASTER);
                set(".wbmp", MIME_IMAGE_WAP_WBMP);
                set(".djv", MIME_IMAGE_VND_DJVU);
                set(".djvu", MIME_IMAGE_VND_DJVU);
                set(".svg", MIME_IMAGE_SVG_XML);
                set(".ief", MIME_IMAGE_IEF);
                set(".cgm", MIME_IMAGE_CGM);
                set(".bmp", MIME_IMAGE_BMP);
                set(".xyz", MIME_CHEMICAL_X_XYZ);
                set(".pdb", MIME_CHEMICAL_X_PDB);
                set(".ra", MIME_AUDIO_X_PN_REALAUDIO);
                set(".ram", MIME_AUDIO_X_PN_REALAUDIO);
                set(".m3u", MIME_AUDIO_X_MPEGURL);
                set(".aifc", MIME_AUDIO_X_AIFF);
                set(".aif", MIME_AUDIO_X_AIFF);
                set(".aiff", MIME_AUDIO_X_AIFF);
                set(".mp3", MIME_AUDIO_MPEG);
                set(".mp2", MIME_AUDIO_MPEG);
                set(".mp1", MIME_AUDIO_MPEG);
                set(".mpga", MIME_AUDIO_MPEG);
                set(".kar", MIME_AUDIO_MIDI);
                set(".mid", MIME_AUDIO_MIDI);
                set(".midi", MIME_AUDIO_MIDI);
                set(".dtd", MIME_APPLICATION_XML_DTD);
                set(".xsl", MIME_APPLICATION_XML);
                set(".xml", MIME_APPLICATION_XML);
                set(".xslt", MIME_APPLICATION_XSLT_XML);
                set(".xht", MIME_APPLICATION_XHTML_XML);
                set(".xhtml", MIME_APPLICATION_XHTML_XML);
                set(".src", MIME_APPLICATION_X_WAIS_SOURCE);
                set(".ustar", MIME_APPLICATION_X_USTAR);
                set(".ms", MIME_APPLICATION_X_TROFF_MS);
                set(".me", MIME_APPLICATION_X_TROFF_ME);
                set(".man", MIME_APPLICATION_X_TROFF_MAN);
                set(".roff", MIME_APPLICATION_X_TROFF);
                set(".tr", MIME_APPLICATION_X_TROFF);
                set(".t", MIME_APPLICATION_X_TROFF);
                set(".texi", MIME_APPLICATION_X_TEXINFO);
                set(".texinfo", MIME_APPLICATION_X_TEXINFO);
                set(".tex", MIME_APPLICATION_X_TEX);
                set(".tcl", MIME_APPLICATION_X_TCL);
                set(".sv4crc", MIME_APPLICATION_X_SV4CRC);
                set(".sv4cpio", MIME_APPLICATION_X_SV4CPIO);
                set(".sit", MIME_APPLICATION_X_STUFFIT);
                set(".swf", MIME_APPLICATION_X_SHOCKWAVE_FLASH);
                set(".shar", MIME_APPLICATION_X_SHAR);
                set(".sh", MIME_APPLICATION_X_SH);
                set(".cdf", MIME_APPLICATION_X_NETCDF);
                set(".nc", MIME_APPLICATION_X_NETCDF);
                set(".latex", MIME_APPLICATION_X_LATEX);
                set(".skm", MIME_APPLICATION_X_KOAN);
                set(".skt", MIME_APPLICATION_X_KOAN);
                set(".skd", MIME_APPLICATION_X_KOAN);
                set(".skp", MIME_APPLICATION_X_KOAN);
                set(".js", MIME_APPLICATION_X_JAVASCRIPT);
                set(".hdf", MIME_APPLICATION_X_HDF);
                set(".gtar", MIME_APPLICATION_X_GTAR);
                set(".spl", MIME_APPLICATION_X_FUTURESPLASH);
                set(".dvi", MIME_APPLICATION_X_DVI);
                set(".dxr", MIME_APPLICATION_X_DIRECTOR);
                set(".dir", MIME_APPLICATION_X_DIRECTOR);
                set(".dcr", MIME_APPLICATION_X_DIRECTOR);
                set(".csh", MIME_APPLICATION_X_CSH);
                set(".cpio", MIME_APPLICATION_X_CPIO);
                set(".pgn", MIME_APPLICATION_X_CHESS_PGN);
                set(".vcd", MIME_APPLICATION_X_CDLINK);
                set(".bcpio", MIME_APPLICATION_X_BCPIO);
                set(".rm", MIME_APPLICATION_VND_RNREALMEDIA);
                set(".ppt", MIME_APPLICATION_VND_MSPOWERPOINT);
                set(".mif", MIME_APPLICATION_VND_MIF);
                set(".grxml", MIME_APPLICATION_SRGS_XML);
                set(".gram", MIME_APPLICATION_SRGS);
                set(".smil", MIME_APPLICATION_RDF_SMIL);
                set(".smi", MIME_APPLICATION_RDF_SMIL);
                set(".rdf", MIME_APPLICATION_RDF_XML);
                set(".ogg", MIME_APPLICATION_X_OGG);
                set(".oda", MIME_APPLICATION_ODA);
                set(".dmg", MIME_APPLICATION_OCTET_STREAM);
                set(".lzh", MIME_APPLICATION_OCTET_STREAM);
                set(".so", MIME_APPLICATION_OCTET_STREAM);
                set(".lha", MIME_APPLICATION_OCTET_STREAM);
                set(".dms", MIME_APPLICATION_OCTET_STREAM);
                set(".bin", MIME_APPLICATION_OCTET_STREAM);
                set(".mathml", MIME_APPLICATION_MATHML_XML);
                set(".cpt", MIME_APPLICATION_MAC_COMPACTPRO);
                set(".hqx", MIME_APPLICATION_MAC_BINHEX40);
                set(".jnlp", MIME_APPLICATION_JNLP);
                set(".ez", MIME_APPLICATION_ANDREW_INSET);
                set(".txt", MIME_TEXT_PLAIN);
                set(".ini", MIME_TEXT_PLAIN);
                set(".c", MIME_TEXT_PLAIN);
                set(".h", MIME_TEXT_PLAIN);
                set(".cpp", MIME_TEXT_PLAIN);
                set(".cxx", MIME_TEXT_PLAIN);
                set(".cc", MIME_TEXT_PLAIN);
                set(".chh", MIME_TEXT_PLAIN);
                set(".java", MIME_TEXT_PLAIN);
                set(".csv", MIME_TEXT_PLAIN);
                set(".bat", MIME_TEXT_PLAIN);
                set(".cmd", MIME_TEXT_PLAIN);
                set(".asc", MIME_TEXT_PLAIN);
                set(".rtf", MIME_TEXT_RTF);
                set(".rtx", MIME_TEXT_RICHTEXT);
                set(".html", MIME_TEXT_HTML);
                set(".htm", MIME_TEXT_HTML);
                set(".zip", MIME_APPLICATION_ZIP);
                set(".rar", MIME_APPLICATION_X_RAR_COMPRESSED);
                set(".gzip", MIME_APPLICATION_X_GZIP);
                set(".gz", MIME_APPLICATION_X_GZIP);
                set(".tgz", MIME_APPLICATION_TGZ);
                set(".tar", MIME_APPLICATION_X_TAR);
                set(".gif", MIME_IMAGE_GIF);
                set(".jpeg", MIME_IMAGE_JPEG);
                set(".jpg", MIME_IMAGE_JPEG);
                set(".jpe", MIME_IMAGE_JPEG);
                set(".tiff", MIME_IMAGE_TIFF);
                set(".tif", MIME_IMAGE_TIFF);
                set(".png", MIME_IMAGE_PNG);
                set(".au", MIME_AUDIO_BASIC);
                set(".snd", MIME_AUDIO_BASIC);
                set(".wav", MIME_AUDIO_X_WAV);
                set(".mov", MIME_VIDEO_QUICKTIME);
                set(".qt", MIME_VIDEO_QUICKTIME);
                set(".mpeg", MIME_VIDEO_MPEG);
                set(".mpg", MIME_VIDEO_MPEG);
                set(".mpe", MIME_VIDEO_MPEG);
                set(".abs", MIME_VIDEO_MPEG);
                set(".doc", MIME_APPLICATION_MSWORD);
                set(".docx", MIME_APPLICATION_MSWORD_2007);
                set(".odt", MIME_APPLICATION_VND_TEXT);
                set(".xls", MIME_APPLICATION_VND_MSEXCEL);
                set(".xlsx", MIME_APPLICATION_VND_MSEXCEL_2007);
                set(".ods", MIME_APPLICATION_VND_SPREADSHEET);
                set(".eps", MIME_APPLICATION_POSTSCRIPT);
                set(".ai", MIME_APPLICATION_POSTSCRIPT);
                set(".ps", MIME_APPLICATION_POSTSCRIPT);
                set(".pdf", MIME_APPLICATION_PDF);
                set(".exe", MIME_APPLICATION_OCTET_STREAM);
                set(".dll", MIME_APPLICATION_OCTET_STREAM);
                set(".class", MIME_APPLICATION_OCTET_STREAM);
                set(".jar", MIME_APPLICATION_JAVA_ARCHIVE);
            }
        };
    }

    static {
        extMapping = new HashMap<String, String>(200) {
            private void set(String key, String value) {
                if (put(key, value) != null) {
                    throw new IllegalArgumentException("Duplicated Mimetype: " + key);
                }
            }

            {
                set(MIME_APPLICATION_VND_MOZZILLA_XUL_XML, ".xul");
                set(MIME_APPLICATION_JSON, ".json");
                set(MIME_X_CONFERENCE_X_COOLTALK, ".ice");
                set(MIME_VIDEO_X_SGI_MOVIE, ".movie");
                set(MIME_VIDEO_X_MSVIDEO, ".avi");
                set(MIME_VIDEO_X_MS_WMV, ".wmv");
                set(MIME_VIDEO_VND_MPEGURL, ".m4u");
                set(MIME_TEXT_X_COMPONENT, ".htc");
                set(MIME_TEXT_X_SETEXT, ".etx");
                set(MIME_TEXT_VND_WAP_WMLSCRIPT, ".wmls");
                set(MIME_TEXT_VND_WAP_XML, ".wml");
                set(MIME_TEXT_TAB_SEPARATED_VALUES, ".tsv");
                set(MIME_TEXT_SGML, ".sgml");
                set(MIME_TEXT_CSS, ".css");
                set(MIME_TEXT_CALENDAR, ".ics");
                set(MIME_MODEL_VRLM, ".vrlm");
                set(MIME_MODEL_MESH, ".mesh");
                set(MIME_MODEL_IGES, ".iges");
                set(MIME_IMAGE_X_RGB, ".rgb");
                set(MIME_IMAGE_X_PORTABLE_PIXMAP, ".ppm");
                set(MIME_IMAGE_X_PORTABLE_GRAYMAP, ".pgm");
                set(MIME_IMAGE_X_PORTABLE_BITMAP, ".pbm");
                set(MIME_IMAGE_X_PORTABLE_ANYMAP, ".pnm");
                set(MIME_IMAGE_X_ICON, ".ico");
                set(MIME_IMAGE_X_CMU_RASTER, ".ras");
                set(MIME_IMAGE_WAP_WBMP, ".wbmp");
                set(MIME_IMAGE_VND_DJVU, ".djvu");
                set(MIME_IMAGE_SVG_XML, ".svg");
                set(MIME_IMAGE_IEF, ".ief");
                set(MIME_IMAGE_CGM, ".cgm");
                set(MIME_IMAGE_BMP, ".bmp");
                set(MIME_CHEMICAL_X_XYZ, ".xyz");
                set(MIME_CHEMICAL_X_PDB, ".pdb");
                set(MIME_AUDIO_X_PN_REALAUDIO, ".ra");
                set(MIME_AUDIO_X_MPEGURL, ".m3u");
                set(MIME_AUDIO_X_AIFF, ".aiff");
                set(MIME_AUDIO_MPEG, ".mp3");
                set(MIME_AUDIO_MIDI, ".midi");
                set(MIME_APPLICATION_XML_DTD, ".dtd");
                set(MIME_APPLICATION_XML, ".xml");
                set(MIME_APPLICATION_XSLT_XML, ".xslt");
                set(MIME_APPLICATION_XHTML_XML, ".xhtml");
                set(MIME_APPLICATION_X_WAIS_SOURCE, ".src");
                set(MIME_APPLICATION_X_USTAR, ".ustar");
                set(MIME_APPLICATION_X_TROFF_MS, ".ms");
                set(MIME_APPLICATION_X_TROFF_ME, ".me");
                set(MIME_APPLICATION_X_TROFF_MAN, ".man");
                set(MIME_APPLICATION_X_TROFF, ".roff");
                set(MIME_APPLICATION_X_TEXINFO, ".texi");
                set(MIME_APPLICATION_X_TEX, ".tex");
                set(MIME_APPLICATION_X_TCL, ".tcl");
                set(MIME_APPLICATION_X_SV4CRC, ".sv4crc");
                set(MIME_APPLICATION_X_SV4CPIO, ".sv4cpio");
                set(MIME_APPLICATION_X_STUFFIT, ".sit");
                set(MIME_APPLICATION_X_SHOCKWAVE_FLASH, ".swf");
                set(MIME_APPLICATION_X_SHAR, ".shar");
                set(MIME_APPLICATION_X_SH, ".sh");
                set(MIME_APPLICATION_X_NETCDF, ".cdf");
                set(MIME_APPLICATION_X_LATEX, ".latex");
                set(MIME_APPLICATION_X_KOAN, ".skm");
                set(MIME_APPLICATION_X_JAVASCRIPT, ".js");
                set(MIME_APPLICATION_X_HDF, ".hdf");
                set(MIME_APPLICATION_X_GTAR, ".gtar");
                set(MIME_APPLICATION_X_FUTURESPLASH, ".spl");
                set(MIME_APPLICATION_X_DVI, ".dvi");
                set(MIME_APPLICATION_X_DIRECTOR, ".dir");
                set(MIME_APPLICATION_X_CSH, ".csh");
                set(MIME_APPLICATION_X_CPIO, ".cpio");
                set(MIME_APPLICATION_X_CHESS_PGN, ".pgn");
                set(MIME_APPLICATION_X_CDLINK, ".vcd");
                set(MIME_APPLICATION_X_BCPIO, ".bcpio");
                set(MIME_APPLICATION_VND_RNREALMEDIA, ".rm");
                set(MIME_APPLICATION_VND_MSPOWERPOINT, ".ppt");
                set(MIME_APPLICATION_VND_MIF, ".mif");
                set(MIME_APPLICATION_SRGS_XML, ".grxml");
                set(MIME_APPLICATION_SRGS, ".gram");
                set(MIME_APPLICATION_RDF_SMIL, ".smil");
                set(MIME_APPLICATION_RDF_XML, ".rdf");
                set(MIME_APPLICATION_X_OGG, ".ogg");
                set(MIME_APPLICATION_ODA, ".oda");
                set(MIME_APPLICATION_MATHML_XML, ".mathml");
                set(MIME_APPLICATION_MAC_COMPACTPRO, ".cpt");
                set(MIME_APPLICATION_MAC_BINHEX40, ".hqx");
                set(MIME_APPLICATION_JNLP, ".jnlp");
                set(MIME_APPLICATION_ANDREW_INSET, ".ez");
                set(MIME_TEXT_PLAIN, ".txt");
                set(MIME_TEXT_RTF, ".rtf");
                set(MIME_TEXT_RICHTEXT, ".rtx");
                set(MIME_TEXT_HTML, ".html");
                set(MIME_APPLICATION_ZIP, ".zip");
                set(MIME_APPLICATION_X_RAR_COMPRESSED, ".rar");
                set(MIME_APPLICATION_X_GZIP, ".gzip");
                set(MIME_APPLICATION_TGZ, ".tgz");
                set(MIME_APPLICATION_X_TAR, ".tar");
                set(MIME_IMAGE_GIF, ".gif");
                set(MIME_IMAGE_JPEG, ".jpg");
                set(MIME_IMAGE_TIFF, ".tiff");
                set(MIME_IMAGE_PNG, ".png");
                set(MIME_AUDIO_BASIC, ".au");
                set(MIME_AUDIO_X_WAV, ".wav");
                set(MIME_VIDEO_QUICKTIME, ".mov");
                set(MIME_VIDEO_MPEG, ".mpg");
                set(MIME_APPLICATION_MSWORD, ".doc");
                set(MIME_APPLICATION_MSWORD_2007, ".docx");
                set(MIME_APPLICATION_VND_TEXT, ".odt");
                set(MIME_APPLICATION_VND_MSEXCEL, ".xls");
                set(MIME_APPLICATION_VND_SPREADSHEET, ".ods");
                set(MIME_APPLICATION_POSTSCRIPT, ".ps");
                set(MIME_APPLICATION_PDF, ".pdf");
                set(MIME_APPLICATION_OCTET_STREAM, ".exe");
                set(MIME_APPLICATION_JAVA_ARCHIVE, ".jar");
            }
        };
    }

    /**
     * Registers MIME type for provided extension. Existing extension type will be overriden.
     */
    public static void registerMimeType(String ext, String mimeType) {
        mimeTypeMapping.put(ext, mimeType);
    }

    /**
     * Returns the corresponding MIME type to the given extension.
     * If no MIME type was found it returns 'application/octet-stream' type.
     */
    public static String getMimeType(String ext) {
        String mimeType = lookupMimeType(ext);
        if (mimeType == null) {
            mimeType = MIME_APPLICATION_OCTET_STREAM;
        }
        return mimeType;
    }

    /**
     * Simply returns MIME type or <code>null</code> if no type is found.
     */
    public static String lookupMimeType(String ext) {
        return mimeTypeMapping.get(ext.toLowerCase());
    }

    /**
     * Simply returns Ext or <code>null</code> if no Mimetype is found.
     */
    public static String lookupExt(String mimeType) {
        return extMapping.get(mimeType.toLowerCase());
    }

    /**
     * Returns the default Ext to the given MimeType.
     * If no MIME type was found it returns 'unknown' ext.
     */
    public static String getDefaultExt(String mimeType) {
        String ext = lookupExt(mimeType);
        if (ext == null) {
            ext = "unknown";
        }
        return ext;
    }
}
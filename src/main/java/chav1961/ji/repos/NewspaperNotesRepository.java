package chav1961.ji.repos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import chav1961.ji.screen.Newspaper.StandardNoteType;
import chav1961.ji.screen.Newspaper.Quarter;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.interfaces.JsonStaxParserLexType;

public class NewspaperNotesRepository {
	private static final String			CAPTION = "caption";
	private static final String			SUBCAPTION = "subcaption";
	private static final String			MIN_YEAR = "minYear";
	private static final String			MAX_YEAR = "maxYear";
	private static final String			CONTENT = "content";
	private static final String			STANDARD = "standard";
	private static final String			TYPE = "type";
	private static final String			YEAR = "year";
	private static final String			QUARTER = "quarter";
	private static final String			BODY = "body";
	
	private final String				caption;
	private final String				subCaption;
	private final int					minYear, maxYear;
	private final NoteRecord[]			content;
	private final StandardNoteRecord[]	standard;
	
	public NewspaperNotesRepository(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		if (repoContent == null) {
			throw new NullPointerException("Repo content parser can't be null");
		}
		else {
			if (repoContent.next() == JsonStaxParserLexType.START_OBJECT) {
				String					_caption = null, _subcaption = null;
				int						_minYear = -1, _maxYear = -1;
				NoteRecord[]			_content = null;
				StandardNoteRecord[]	_standard = null;
				
loop:			for (JsonStaxParserLexType item : repoContent) {
					switch(item) {
						case LIST_SPLITTER	:
							break;
						case END_OBJECT	:
							break loop;
						case NAME		:
							switch (repoContent.name()) {
								case CAPTION	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
										_caption = repoContent.stringValue();
									}
									break;
								case SUBCAPTION	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
										_subcaption = repoContent.stringValue();
									}
									break;
								case MIN_YEAR	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.INTEGER_VALUE) {
										_minYear = (int) repoContent.intValue();
									}
									break;
								case MAX_YEAR	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.INTEGER_VALUE) {
										_maxYear = (int) repoContent.intValue();
									}
									break;
								case CONTENT	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.START_ARRAY) {
										_content = loadContent(repoContent);
										if (repoContent.current() != JsonStaxParserLexType.END_ARRAY) {
											throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: ']' awaited");
										}
									}
									break;
								case STANDARD	:
									if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.START_ARRAY) {
										_standard = loadStandard(repoContent);
										if (repoContent.current() != JsonStaxParserLexType.END_ARRAY) {
											throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: ']' awaited");
										}
									}
									break;
								default :
									throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: unknown field name ["+repoContent.name()+"]");
							}
							break;
						default :
							throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: name or '}' awaited");
					}
				}
				if (_caption == null) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+CAPTION+"] not found");
				}
				else {
					this.caption = _caption;
				}
				if (_subcaption == null) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+SUBCAPTION+"] not found");
				}
				else {
					this.subCaption = _subcaption;
				}
				if (_minYear < 0) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+MIN_YEAR+"] not found");
				}
				else {
					this.minYear = _minYear;
				}
				if (_maxYear < 0) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+MAX_YEAR+"] not found");
				}
				else {
					this.maxYear = _maxYear;
				}
				if (_content == null) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+CONTENT+"] not found");
				}
				else {
					this.content = _content;
				}
				if (_standard == null) {
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+STANDARD+"] not found");
				}
				else {
					this.standard = _standard;
					Arrays.sort(this.standard);
					
					for (StandardNoteType item : StandardNoteType.values()) {
						final NewspaperNote	note = seekStandardNote(item);
						
						if (note == null) {
							throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: standard notes does't contain mandatory item ["+item+"]");
						}
					}
				}
			}
			else {
				throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: '{' awaited");
			}
		}
	}

	public int getSize(final int year, final Quarter quarter) {
		for (NewspaperNotesRepository.NoteRecord item : content) {
			if (item.year == year && item.quarter == quarter) {
				return item.notes.length;
			}
		}
		return 0;
	}
	
	public NewspaperNote getNote(final int year, final Quarter quarter, final int noteIndex) {
		for (NewspaperNotesRepository.NoteRecord item : content) {
			if (item.year == year && item.quarter == quarter) {
				return item.notes[noteIndex];
			}
		}
		return null;
	}

	public String getCaption() {
		return caption;
	}

	public String getSubCaption() {
		return subCaption;
	}
	
	public int getMinYear() {
		return minYear;
	}

	public int getMaxYear() {
		return maxYear;
	}
	
	public NewspaperNote getTypedNote(final StandardNoteType type, final Object... parameters) {
		if (type == null) {
			throw new NullPointerException("Note type can't be null");
		}
		else {
			final NewspaperNote	note = seekStandardNote(type);

			return new NewspaperNote(note.getCaption(), String.format(note.getBody(),parameters));
		}
	}
	
	@Override
	public String toString() {
		return "NoteRepository [notes=" + Arrays.toString(content) + "]";
	}

	private static NoteRecord[] loadContent(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		final List<NoteRecord>	result = new ArrayList<>();
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_ARRAY		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case START_OBJECT	:
					result.add(loadYearContent(repoContent));
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: '{' awaited");
			}
		}
		return result.toArray(new NoteRecord[result.size()]);
	}

	private static NoteRecord loadYearContent(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		int				_year = -1;
		Quarter 		_quarter = null;
		NewspaperNote[]	_notes = null;
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_OBJECT		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case NAME			:
					switch (repoContent.name()) {
						case YEAR		:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.INTEGER_VALUE) {
								_year = (int) repoContent.intValue();
							}
							break;
						case QUARTER	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
								_quarter = Quarter.valueOf(repoContent.stringValue());
							}
							break;
						case CONTENT	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.START_ARRAY) {
								_notes = loadRecord(repoContent);
								if (repoContent.current() != JsonStaxParserLexType.END_ARRAY) {
									throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: ']' awaited");
								}
							}
							break;
						default :
							throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: unknown field name ["+repoContent.name()+"]");
					}
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: name or '}' awaited");
			}
		}
		if (_year < 0) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+YEAR+"] not found");
		}
		else if (_quarter == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+QUARTER+"] not found");
		}
		else if (_notes == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+CONTENT+"] not found");
		}
		else {
			return new NoteRecord(_year, _quarter, _notes);
		}
	}
	
	private static NewspaperNote[] loadRecord(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		final List<NewspaperNote>	result = new ArrayList<>();
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_ARRAY		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case START_OBJECT	:
					result.add(loadNoteContent(repoContent));
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: '{' awaited");
			}
		}
		return result.toArray(new NewspaperNote[result.size()]);
	}

	private static NewspaperNote loadNoteContent(final JsonStaxParser repoContent) throws SyntaxException, IllegalStateException, IOException {
		String	_caption = null;
		String	_body = null;
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_OBJECT		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case NAME			:
					switch (repoContent.name()) {
						case CAPTION	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
								_caption = repoContent.stringValue();
							}
							break;
						case BODY	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
								_body = repoContent.stringValue();
							}
							break;
						default :
							throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: unknown field name ["+repoContent.name()+"]");
					}
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: name or '}' awaited");
			}
		}
		if (_caption == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+CAPTION+"] not found");
		}
		else if (_body == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+BODY+"] not found");
		}
		else {
			return new NewspaperNote(_caption, _body);
		}
	}

	private static StandardNoteRecord[] loadStandard(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		final List<StandardNoteRecord>	result = new ArrayList<>();
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_ARRAY		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case START_OBJECT	:
					result.add(loadStandardNote(repoContent));
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: '{' awaited");
			}
		}
		return result.toArray(new StandardNoteRecord[result.size()]);
	}

	private static StandardNoteRecord loadStandardNote(final JsonStaxParser repoContent) throws SyntaxException, IOException {
		StandardNoteType	_type = null;
		NewspaperNote		_body = null;
		
loop:	for (JsonStaxParserLexType item : repoContent) {
			switch (item) {
				case END_OBJECT		:
					break loop;
				case LIST_SPLITTER	:
					break;
				case NAME			:
					switch (repoContent.name()) {
						case TYPE	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.STRING_VALUE) {
								_type = StandardNoteType.valueOf(repoContent.stringValue());
							}
							break;
						case CONTENT	:
							if (repoContent.next() == JsonStaxParserLexType.NAME_SPLITTER && repoContent.next() == JsonStaxParserLexType.START_OBJECT) {
								_body = loadNoteContent(repoContent);
							}
							break;
						default :
							throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: unknown field name ["+repoContent.name()+"]");
					}
					break;
				default:
					throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: name or '}' awaited");
			}
		}
		if (_type == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+TYPE+"] not found");
		}
		else if (_body == null) {
			throw new SyntaxException(repoContent.row(), repoContent.col(), "Illegal Json content: mandatory field ["+CONTENT+"] not found");
		}
		else {
			return new StandardNoteRecord(_type, _body);
		}
	}

	private NewspaperNote seekStandardNote(final StandardNoteType item) {
		final StandardNoteRecord	rec = new StandardNoteRecord(item, null);
		
		return standard[Arrays.binarySearch(standard,rec)].note;
	}
	
	public static class NewspaperNote {
		private final String	caption;
		private final String 	body;
		
		public NewspaperNote(final String caption, final String body) {
			this.caption = caption.endsWith("\n") ? caption : caption+'\n';
			this.body = body.endsWith("\n") ? body : body+'\n';
		}

		public String getCaption() {
			return caption;
		}

		public String getBody() {
			return body;
		}

		@Override
		public String toString() {
			return "Note [caption=" + caption + ", body=" + body + "]";
		}
	}	


	private static class NoteRecord {
		final int		year;
		final Quarter	quarter;
		final NewspaperNote[]	notes;
		
		private NoteRecord(final int year, final Quarter quarter, final NewspaperNote[] notes) {
			this.year = year;
			this.quarter = quarter;
			this.notes = notes;
		}

		@Override
		public String toString() {
			return "NoteRecord [year=" + year + ", quarter=" + quarter + ", notes=" + Arrays.toString(notes) + "]";
		}
	}
	
	private static class StandardNoteRecord implements Comparable<StandardNoteRecord> {
		final StandardNoteType	type;
		final NewspaperNote		note;
		
		private StandardNoteRecord(final StandardNoteType type, final NewspaperNote note) {
			this.type = type;
			this.note = note;
		}

		@Override
		public int compareTo(StandardNoteRecord o) {
			return o.type.ordinal() - type.ordinal();
		}
		
		@Override
		public String toString() {
			return "StandardNoteRecord [type=" + type + ", note=" + note + "]";
		}

	}
	
}
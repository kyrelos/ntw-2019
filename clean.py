"""clean and stem the text data"""
import re
from porter_stemmer import PorterStemmer

STEM = PorterStemmer()

def repl_mpesa_ids(s:str, sub="xxmpesa confirmed") -> str:
    """Mpesa have unique IDs that needs replacing"""
    return re.sub(r"(^(\w)* confirm(\w)*)|(\w{32})", sub, s)

def repl_phone_nums(s:str, sub="xxphonenum") -> str:
    """Replace the hashed phone numbers"""
    return re.sub(r"^(\w{32})", sub, s)

def repl_num(s:str, sub="xxnum") -> str:
    """Removed digits"""
    if s.isdigit():
        return sub
    return s

def repl_measures(s:str, sub="xxmeasure") -> str:
    """measures such 1pm, 12am, 12kg, 12mb, 19th, 12:23 """
    return re.sub(r"((^(\d)+(\w)+$)|(\d+:\d+))", sub, s)

def repl_money(s:str, sub="xxcurr") -> str:
    """Remove money e.g Ksh12, Ksh3,227.00"""
    return re.sub(r"(((\w){2,4}(\d)+$)|(((\w){2,4})?(\d+)\,(\d+)\.(\d+))|(((\w){2,4})?(\d+)\.(\d+))|(\d+/\d+/\d+))", sub, s)

def word_stemming(word) -> iter:
    """Stemming using https://tartarus.org/martin/PorterStemmer/"""
    if not word:
        return word
    new_words = re.sub('[^0-9a-zA-Z]+', ' ', word.lower())
    return (STEM.stem(c, 0,len(c)-1) for c in new_words.split())

def preprocess(
        words: list,
        replace_unique=True,
        stem=True,
        replace_nums=True,
        replace_measures=True) -> iter:

    # Generalize money occurrence
    words = (repl_money(word) for word in words)

    # Generalize
    words = (repl_phone_nums(word) for word in words)

    # stemming
    if stem:
        new_words = ()
        for w in words:
            word_token = word_stemming(w)
            new_words += tuple(word_token)
        words = new_words
        del new_words

    # generalize all numbers
    if replace_nums:
        words = (repl_num(word) for word in words)

    # generalize all measures
    if replace_measures:
        words = (repl_measures(word) for word in words)

    return (word for word in words if word)

def clean_stem(df_col):
    """Clean the column data and stem it"""
    return df_col \
            .apply(lambda x: x.lower()) \
            .apply(repl_mpesa_ids) \
            .apply(lambda x: " ".join(preprocess(x.split())))  # Clean and stem the column


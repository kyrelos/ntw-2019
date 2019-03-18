"""Scrap messages from output of android app: `SMS Backup & Restore`

Example::

    $ python scrapper.py sms-20190303110043.xml

The above example will ouput to a file sms-20190303110043.csv that is adjacent
to the input.
"""
import argparse
import csv
import hashlib
import html
import re
from xml.etree.ElementTree import fromstring
from xmljson import badgerfish as bf


MSISDN_REGEX = "((\+254(\d)*)|(07(\d*))|(254(\d*)))"
XML_HTML_UNICODE_REGEX = "(&#(\d)*;)"
IMPORTANT_KEYS = (
    ("@address", "address"),
    ("@body", "body"),
    ("@contact_name", "in_address_book")
)
NOT_SPAM_ADDRESSES = ("MPESA", "KCB", "Equity Bank")
LABEL_KEY = "is_spam"


def hash_msisdn(msisdn):
    "hashes on 'Kenyan' numbers"
    if msisdn.startswith("07"):
        msisdn = "254{}".format(msisdn[1:])
    elif msisdn.startswith("+254"):
        msisdn = msisdn[1:]
    elif not msisdn.startswith("254"):
        return msisdn
    return hashlib.md5(msisdn.encode()).hexdigest()


def repl_msisdn(match_obj):
    return hash_msisdn(match_obj.group(0))


def repl_html_chars(match_obj):
    return html.unescape(match_obj.group(0))


def label_sms(sms: dict) -> int:
    """Label the collected sms

    Assume all outbox are not spam, otherwise it is spam"""
    label = 1
    if sms["@type"] == 2:
        return 0
    if sms["@address"] in NOT_SPAM_ADDRESSES:
        return 0
    return label


def compress_sms(sms: dict) -> dict:
    """Only use important features"""
    contact_name = sms["@contact_name"]
    sms["@contact_name"] = 1 if contact_name != "(Unknown)" else 0
    payload = {}
    for key, new_key in IMPORTANT_KEYS:
        payload[new_key] = sms[key]
    payload[LABEL_KEY] = label_sms(sms)
    return payload


def process_file(f_obj) -> dict:
    """Obfuscate MSISDNS and get relevant data"""
    raw_data: str = f_obj.read()
    cleaned_data = re.sub(MSISDN_REGEX, repl_msisdn, raw_data)
    cleaned_data = re.sub(
        XML_HTML_UNICODE_REGEX, repl_html_chars, cleaned_data)
    data = bf.data(fromstring(cleaned_data.encode()))
    smses = data["smses"]["sms"]
    lean_sms = [compress_sms(sms) for sms in smses]
    return lean_sms


def write_to_csv(filename: str, data: dict) -> None:
    with open(filename, "w") as csvfile:
        fieldnames = tuple((mapped_key for _, mapped_key in IMPORTANT_KEYS))
        fieldnames += (LABEL_KEY,)
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames,
                                quoting=csv.QUOTE_ALL)
        writer.writeheader()
        writer.writerows(data)


if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "input",
        type=argparse.FileType("r"),
        help="File with SMS data in xml format"
    )
    args = parser.parse_args()
    data = process_file(args.input)
    filename = "{}.csv".format(".".join(args.input.name.split(".")[:-1]))
    write_to_csv(filename, data)

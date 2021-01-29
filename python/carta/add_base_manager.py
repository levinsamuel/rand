from pathlib import Path
from typing import TextIO
import re

basedir = Path(__file__).parent.parent.parent


def get_path(class_name: str):
    path = '/'.join(class_name.split('.')[:-1])
    return path + '.py'


def get_class_name(class_name: str):
    clz = class_name.split('.')[-1]
    return clz


def get_class_line(f: TextIO, clz: str, start_line: int = 0):
    re_class = re.compile(r'^class {}'.format(clz))
    for i, line in enumerate(f):
        if i < start_line:
            continue
        if re_class.search(line):
            return line, i
    return None, -1


if __name__ == '__main__':
    model = 'eshares.investor_services.models.ConvertibleNoteValue'
    path = get_path(model)
    print(path)
    clz = get_class_name(model)
    print(clz)
    p = Path(str(basedir) + '/' + path)
    with open(p, 'r') as f:
        class_line, class_place = get_class_line(f, clz)
        next_class_line, next_class_place = get_class_line(f, '', class_place + 1)
        print(class_line, ': ', class_place, sep='')
        print(next_class_line, ': ', next_class_place, sep='')


models = ['eshares.investor_services.models.ConvertibleNoteValue',
 'eshares.issuables.warrants.models.WarrantVestingManager',
 'eshares.issuables.options_app.models.grant_vesting_event.GrantVestingEvent',
 'eshares.financings.models.FinancingInvestor',
 'eshares.payments.models.PaperCheckTransaction',
 'eshares.transactions.models.MoneyReceipt',
 'eshares.common.files.models.FileGroupMember',
 'eshares.investor_services.models.ClosingDocsRequest',
 'eshares.board.models.board_member.BoardMember',
 'eshares.organizations.models.EntityOrgPermission',
 'eshares.corporations.models.legacy_models.Corporation',
 'eshares.issuables.warrants.models.DraftWarrant',
 'eshares.issuables.convertible_notes.models.convertible_note.ConvertibleNote',
 'eshares.payments.models.PaperCheckAccount',
 'eshares.corporations.models.legacy_models.FundingRequest',
 'eshares.issuables.options_app.models.option_grant.OptionGrant',
 'eshares.tender_offers.models.tender_offer.TenderOffer',
 'eshares.corporations.models.legacy_models.CorporationRole',
 'eshares.issuables.certificates.models.certificate_vesting_event.CertificateVestingEvent',
 'eshares.payments.models.Transfer',
 'eshares.corporations.stakeholders.models.Stakeholder',
 'eshares.investor_services.models.FinancialsRequest',
 'eshares.issuables.options_app.models.option_plan.OptionPlan',
 'eshares.investor_services.models.CapitalCall',
 'eshares.investor_services.models.KPIsRequest',
 'eshares.issuables.options_app.models.tax_withholding.TaxWithholding',
 'eshares.issuables.warrants.models.WarrantVestingEvent',
 'eshares.deal_rooms.models.DealRoomParticipant',
 'eshares.issuables.convertible_notes.models.draft_convertible_note.DraftConvertibleNote',
 'eshares.investor_services.models.CapTableRequest',
 'eshares.financings.models.Financing',
 'eshares.issuables.warrants.models.Warrant',
 'eshares.payments.models.Dividend',
 'eshares.payments.models.Transaction',
 'eshares.investor_services.models.TemporaryPermission',
 'eshares.issuables.certificates.models.certificate.Certificate',
 'eshares.payments.models.PaperCheckTransfer',
 'eshares.investor_services.models.ShareClassPrice',
 'eshares.common.files.models.UploadedFile',
 'eshares.organizations.models.OrganizationMembership',
 'eshares.common.files.models.UploadedFileChunkGroup',
 'eshares.issuables.options_app.models.draft_option_grant.DraftOptionGrant',
 'eshares.issuables.options_app.models.draft_rsu.DraftRSU',
 'eshares.issuables.options_app.models.draft_cbu.DraftCBU',
 'eshares.issuables.options_app.models.draft_sar.DraftSAR',
 'eshares.corporations.models.legacy_models.CorporationUserRoles',
 'eshares.common.files.models.UploadedFileChunk',
 'eshares.issuables.certificates.models.draft_certificate.DraftCertificate',
 'eshares.issuables.certificates.models.draft_rsa.DraftRSA',
 'eshares.issuables.certificates.models.draft_piu.DraftPIU',
 'eshares.public_markets.models.files.PublicMarketsUploadedFileChunkGroup',
 'eshares.public_markets.models.files.PublicMarketsFileGroupMember',
 'eshares.public_markets.models.files.PublicMarketsUploadedFile',
 'eshares.public_markets.models.accounts.StockTransaction',
 'eshares.public_markets.models.accounts.StockAccount',
 'eshares.public_markets.espp.models.espp_offering_participant.ESPPOfferingParticipant',
 'eshares.public_markets.models.accounts.ShareLot',
 'eshares.public_markets.models.accounts.StockTransfer',
 'eshares.public_markets.models.files.PublicMarketsUploadedFileChunk']

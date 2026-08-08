package emu.grasscutter.server.packet.send;

import emu.grasscutter.game.mail.Mail;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.net.packet.*;
import emu.grasscutter.net.proto.EquipParamOuterClass.EquipParam;
import emu.grasscutter.net.proto.MailChangeNotifyOuterClass.MailChangeNotify;
import emu.grasscutter.net.proto.MailDataOuterClass.MailData;
import emu.grasscutter.net.proto.MailItemOuterClass.MailItem;
import emu.grasscutter.net.proto.MailTextContentOuterClass.MailTextContent;
import java.util.*;

public class PacketMailChangeNotify extends BasePacket {

	/*
	 * A single Mail means a newly received mail.
	 */
	public PacketMailChangeNotify(Player player, Mail message) {
		this(player, Collections.singletonList(message), null);
	}

	/*
	 * A List<Mail> is used by read, star and attachment-claim handlers.
	 * Those are changes to mail already known by the client. 6.7 does not
	 * distinguish these from new mail on the wire, so no flag is needed.
	 */
	public PacketMailChangeNotify(Player player, List<Mail> changedMailList) {
		this(player, changedMailList, null);
	}

	/*
	 * Currently used by the deletion path with mailList == null.
	 */
	public PacketMailChangeNotify(Player player, List<Mail> mailList, List<Integer> delMailIdList) {
        super(PacketOpcodes.MailChangeNotify);

        var proto = MailChangeNotify.newBuilder();

        if (mailList != null) {
            for (Mail message : mailList) {
                var mailTextContent = MailTextContent.newBuilder();
                mailTextContent.setTitle(message.mailContent.title);
                mailTextContent.setContent(message.mailContent.content);
                mailTextContent.setSender(message.mailContent.sender);

                List<MailItem> mailItems = new ArrayList<>();

                for (Mail.MailItem item : message.itemList) {
                    var mailItem = MailItem.newBuilder();
                    var itemParam = EquipParam.newBuilder();
                    itemParam.setItemId(item.itemId);
                    itemParam.setItemNum(item.itemCount);
                    mailItem.setEquipParam(itemParam.build());

                    mailItems.add(mailItem.build());
                }

                var mailData = MailData.newBuilder();
                mailData.setMailId(player.getMailHandler().toClientMailId(player.getMailId(message)));
                mailData.setMailTextContent(mailTextContent.build());
                mailData.addAllItemList(mailItems);
                mailData.setSendTime((int) message.sendTime);
                mailData.setExpireTime((int) message.expireTime);
                mailData.setImportance(message.importance);
                mailData.setIsRead(message.isRead);
                mailData.setIsAttachmentGot(message.isAttachmentGot);
                mailData.setCollectStateValue(message.stateValue);

                // 6.6's MailChangeNotify carried new and changed mail in separate repeated fields
                // (mail_list / change_mail_list). 6.7 only has mail_list, so both go there.
                proto.addMailList(mailData.build());
            }
        }

        if (delMailIdList != null) {
            proto.addAllDelMailIdList(delMailIdList);
        }

        this.setData(proto.build());
    }
}

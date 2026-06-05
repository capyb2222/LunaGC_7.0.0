
package emu.grasscutter.net.proto;

public final class QuestCreateEntityRspOuterClass {
  private QuestCreateEntityRspOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface QuestCreateEntityRspOrBuilder extends

      com.google.protobuf.MessageOrBuilder {

    int getEntityId();

    int getQuestId();

    boolean getIsRewind();

    int getParentQuestId();

    int getRetcode();

    boolean hasEntity();

    emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo getEntity();

    emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder getEntityOrBuilder();
  }

  public static final class QuestCreateEntityRsp extends
      com.google.protobuf.GeneratedMessageV3 implements

      QuestCreateEntityRspOrBuilder {
  private static final long serialVersionUID = 0L;

    private QuestCreateEntityRsp(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private QuestCreateEntityRsp() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new QuestCreateEntityRsp();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private QuestCreateEntityRsp(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 56: {

              isRewind_ = input.readBool();
              break;
            }
            case 64: {

              entityId_ = input.readUInt32();
              break;
            }
            case 74: {
              emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder subBuilder = null;
              if (entity_ != null) {
                subBuilder = entity_.toBuilder();
              }
              entity_ = input.readMessage(emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(entity_);
                entity_ = subBuilder.buildPartial();
              }

              break;
            }
            case 88: {

              parentQuestId_ = input.readUInt32();
              break;
            }
            case 96: {

              questId_ = input.readUInt32();
              break;
            }
            case 120: {

              retcode_ = input.readInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.internal_static_QuestCreateEntityRsp_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.internal_static_QuestCreateEntityRsp_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.class, emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.Builder.class);
    }

    public static final int ENTITY_ID_FIELD_NUMBER = 3;
    private int entityId_;

    @java.lang.Override
    public int getEntityId() {
      return entityId_;
    }

    public static final int QUEST_ID_FIELD_NUMBER = 2;
    private int questId_;

    @java.lang.Override
    public int getQuestId() {
      return questId_;
    }

    public static final int IS_REWIND_FIELD_NUMBER = 9;
    private boolean isRewind_;

    @java.lang.Override
    public boolean getIsRewind() {
      return isRewind_;
    }

    public static final int PARENT_QUEST_ID_FIELD_NUMBER = 10;
    private int parentQuestId_;

    @java.lang.Override
    public int getParentQuestId() {
      return parentQuestId_;
    }

    public static final int RETCODE_FIELD_NUMBER = 5;
    private int retcode_;

    @java.lang.Override
    public int getRetcode() {
      return retcode_;
    }

    public static final int ENTITY_FIELD_NUMBER = 6;
    private emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo entity_;

    @java.lang.Override
    public boolean hasEntity() {
      return entity_ != null;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo getEntity() {
      return entity_ == null ? emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.getDefaultInstance() : entity_;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder getEntityOrBuilder() {
      return getEntity();
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (isRewind_ != false) {
        output.writeBool(9, isRewind_);
      }
      if (entityId_ != 0) {
        output.writeUInt32(3, entityId_);
      }
      if (entity_ != null) {
        output.writeMessage(9, getEntity());
      }
      if (parentQuestId_ != 0) {
        output.writeUInt32(10, parentQuestId_);
      }
      if (questId_ != 0) {
        output.writeUInt32(2, questId_);
      }
      if (retcode_ != 0) {
        output.writeInt32(5, retcode_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (isRewind_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(9, isRewind_);
      }
      if (entityId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(3, entityId_);
      }
      if (entity_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(9, getEntity());
      }
      if (parentQuestId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(10, parentQuestId_);
      }
      if (questId_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, questId_);
      }
      if (retcode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, retcode_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp)) {
        return super.equals(obj);
      }
      emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp other = (emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp) obj;

      if (getEntityId()
          != other.getEntityId()) return false;
      if (getQuestId()
          != other.getQuestId()) return false;
      if (getIsRewind()
          != other.getIsRewind()) return false;
      if (getParentQuestId()
          != other.getParentQuestId()) return false;
      if (getRetcode()
          != other.getRetcode()) return false;
      if (hasEntity() != other.hasEntity()) return false;
      if (hasEntity()) {
        if (!getEntity()
            .equals(other.getEntity())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ENTITY_ID_FIELD_NUMBER;
      hash = (53 * hash) + getEntityId();
      hash = (37 * hash) + QUEST_ID_FIELD_NUMBER;
      hash = (53 * hash) + getQuestId();
      hash = (37 * hash) + IS_REWIND_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getIsRewind());
      hash = (37 * hash) + PARENT_QUEST_ID_FIELD_NUMBER;
      hash = (53 * hash) + getParentQuestId();
      hash = (37 * hash) + RETCODE_FIELD_NUMBER;
      hash = (53 * hash) + getRetcode();
      if (hasEntity()) {
        hash = (37 * hash) + ENTITY_FIELD_NUMBER;
        hash = (53 * hash) + getEntity().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements

        emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRspOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.internal_static_QuestCreateEntityRsp_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.internal_static_QuestCreateEntityRsp_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.class, emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        entityId_ = 0;

        questId_ = 0;

        isRewind_ = false;

        parentQuestId_ = 0;

        retcode_ = 0;

        if (entityBuilder_ == null) {
          entity_ = null;
        } else {
          entity_ = null;
          entityBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.internal_static_QuestCreateEntityRsp_descriptor;
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp getDefaultInstanceForType() {
        return emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.getDefaultInstance();
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp build() {
        emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp buildPartial() {
        emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp result = new emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp(this);
        result.entityId_ = entityId_;
        result.questId_ = questId_;
        result.isRewind_ = isRewind_;
        result.parentQuestId_ = parentQuestId_;
        result.retcode_ = retcode_;
        if (entityBuilder_ == null) {
          result.entity_ = entity_;
        } else {
          result.entity_ = entityBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp) {
          return mergeFrom((emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp other) {
        if (other == emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp.getDefaultInstance()) return this;
        if (other.getEntityId() != 0) {
          setEntityId(other.getEntityId());
        }
        if (other.getQuestId() != 0) {
          setQuestId(other.getQuestId());
        }
        if (other.getIsRewind() != false) {
          setIsRewind(other.getIsRewind());
        }
        if (other.getParentQuestId() != 0) {
          setParentQuestId(other.getParentQuestId());
        }
        if (other.getRetcode() != 0) {
          setRetcode(other.getRetcode());
        }
        if (other.hasEntity()) {
          mergeEntity(other.getEntity());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int entityId_ ;

      @java.lang.Override
      public int getEntityId() {
        return entityId_;
      }

      public Builder setEntityId(int value) {

        entityId_ = value;
        onChanged();
        return this;
      }

      public Builder clearEntityId() {

        entityId_ = 0;
        onChanged();
        return this;
      }

      private int questId_ ;

      @java.lang.Override
      public int getQuestId() {
        return questId_;
      }

      public Builder setQuestId(int value) {

        questId_ = value;
        onChanged();
        return this;
      }

      public Builder clearQuestId() {

        questId_ = 0;
        onChanged();
        return this;
      }

      private boolean isRewind_ ;

      @java.lang.Override
      public boolean getIsRewind() {
        return isRewind_;
      }

      public Builder setIsRewind(boolean value) {

        isRewind_ = value;
        onChanged();
        return this;
      }

      public Builder clearIsRewind() {

        isRewind_ = false;
        onChanged();
        return this;
      }

      private int parentQuestId_ ;

      @java.lang.Override
      public int getParentQuestId() {
        return parentQuestId_;
      }

      public Builder setParentQuestId(int value) {

        parentQuestId_ = value;
        onChanged();
        return this;
      }

      public Builder clearParentQuestId() {

        parentQuestId_ = 0;
        onChanged();
        return this;
      }

      private int retcode_ ;

      @java.lang.Override
      public int getRetcode() {
        return retcode_;
      }

      public Builder setRetcode(int value) {

        retcode_ = value;
        onChanged();
        return this;
      }

      public Builder clearRetcode() {

        retcode_ = 0;
        onChanged();
        return this;
      }

      private emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo entity_;
      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder> entityBuilder_;

      public boolean hasEntity() {
        return entityBuilder_ != null || entity_ != null;
      }

      public emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo getEntity() {
        if (entityBuilder_ == null) {
          return entity_ == null ? emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.getDefaultInstance() : entity_;
        } else {
          return entityBuilder_.getMessage();
        }
      }

      public Builder setEntity(emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo value) {
        if (entityBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          entity_ = value;
          onChanged();
        } else {
          entityBuilder_.setMessage(value);
        }

        return this;
      }

      public Builder setEntity(
          emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder builderForValue) {
        if (entityBuilder_ == null) {
          entity_ = builderForValue.build();
          onChanged();
        } else {
          entityBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }

      public Builder mergeEntity(emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo value) {
        if (entityBuilder_ == null) {
          if (entity_ != null) {
            entity_ =
              emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.newBuilder(entity_).mergeFrom(value).buildPartial();
          } else {
            entity_ = value;
          }
          onChanged();
        } else {
          entityBuilder_.mergeFrom(value);
        }

        return this;
      }

      public Builder clearEntity() {
        if (entityBuilder_ == null) {
          entity_ = null;
          onChanged();
        } else {
          entity_ = null;
          entityBuilder_ = null;
        }

        return this;
      }

      public emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder getEntityBuilder() {

        onChanged();
        return getEntityFieldBuilder().getBuilder();
      }

      public emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder getEntityOrBuilder() {
        if (entityBuilder_ != null) {
          return entityBuilder_.getMessageOrBuilder();
        } else {
          return entity_ == null ?
              emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.getDefaultInstance() : entity_;
        }
      }

      private com.google.protobuf.SingleFieldBuilderV3<
          emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder>
          getEntityFieldBuilder() {
        if (entityBuilder_ == null) {
          entityBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfo.Builder, emu.grasscutter.net.proto.CreateEntityInfoOuterClass.CreateEntityInfoOrBuilder>(
                  getEntity(),
                  getParentForChildren(),
                  isClean());
          entity_ = null;
        }
        return entityBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }

    }

    private static final emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp();
    }

    public static emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<QuestCreateEntityRsp>
        PARSER = new com.google.protobuf.AbstractParser<QuestCreateEntityRsp>() {
      @java.lang.Override
      public QuestCreateEntityRsp parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new QuestCreateEntityRsp(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<QuestCreateEntityRsp> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<QuestCreateEntityRsp> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public emu.grasscutter.net.proto.QuestCreateEntityRspOuterClass.QuestCreateEntityRsp getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_QuestCreateEntityRsp_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_QuestCreateEntityRsp_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032QuestCreateEntityRsp.proto\032\026CreateEnti" +
      "tyInfo.proto\"\233\001\n\024QuestCreateEntityRsp\022\021\n" +
      "\tentity_id\030\010 \001(\r\022\020\n\010quest_id\030\014 \001(\r\022\021\n\tis" +
      "_rewind\030\007 \001(\010\022\027\n\017parent_quest_id\030\013 \001(\r\022\017" +
      "\n\007retcode\030\017 \001(\005\022!\n\006entity\030\t \001(\0132\021.Create" +
      "EntityInfoB\033\n\031emu.grasscutter.net.protob" +
      "\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          emu.grasscutter.net.proto.CreateEntityInfoOuterClass.getDescriptor(),
        });
    internal_static_QuestCreateEntityRsp_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_QuestCreateEntityRsp_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_QuestCreateEntityRsp_descriptor,
        new java.lang.String[] { "EntityId", "QuestId", "IsRewind", "ParentQuestId", "Retcode", "Entity", });
    emu.grasscutter.net.proto.CreateEntityInfoOuterClass.getDescriptor();
  }

}
